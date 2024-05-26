package pro.azhidkov.mariotte.cases.apps.guest.reservation

import io.kotest.matchers.shouldBe
import io.restassured.module.kotlin.extensions.Then
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import pro.azhidkov.mariotte.assertions.shouldMatch
import pro.azhidkov.mariotte.clients.Guest
import pro.azhidkov.mariotte.core.hotels.rooms.RoomType
import pro.azhidkov.mariotte.fixtures.HotelsObjectMother
import pro.azhidkov.mariotte.fixtures.RoomsObjectMother
import pro.azhidkov.mariotte.fixtures.ReservationsObjectMother.roomReservationRequest
import pro.azhidkov.mariotte.fixtures.nearFutureDate
import pro.azhidkov.mariotte.infra.spring.MariotteBaseTest
import pro.azhidkov.mariotte.backgrounds.Backgrounds
import pro.azhidkov.mariotte.fixtures.ReservationsObjectMother.createRoomReservationRequestJson
import java.time.LocalDate


@DisplayName("Бронирование номера")
class RoomReservationTest(
    @Autowired private val backgrounds: Backgrounds
) : MariotteBaseTest() {

    @DisplayName("должно возвращать идентификатор брони, по которому можно получить детали брони")
    @Test
    fun reservationPersistenceTest() {
        // Given
        val roomType = RoomType.LUX
        val createOrderRequest = roomReservationRequest(hotelId = HotelsObjectMother.theHotel().id!!, roomType = roomType)
        val guest = Guest.loginAsTheGuest()

        // When
        val reservationSuccess = guest.reservations.reserveRoom(createOrderRequest)

        // And when
        val reservation = guest.reservations.getReservation(reservationSuccess.reservationId)

        // Then
        reservation shouldMatch createOrderRequest
    }

    @DisplayName("должно не позволять бронировать номера по несуществующему иду отеля")
    @Test
    fun reservationInNotExistingHotel() {
        // Given
        val notExistingHotelId = 404
        val roomReservationRequest = roomReservationRequest(hotelId = notExistingHotelId)
        val guest = Guest.loginAsTheGuest()

        // When
        val errorResponse = guest.reservations.reserveRoomForError(roomReservationRequest, HttpStatus.CONFLICT)

        // Then
        errorResponse.status shouldBe HttpStatus.CONFLICT.value()
        errorResponse.type.path shouldBe "hotel-not-found"
    }

    @DisplayName("должна не позволять бронировать номера типа, не представленного в отеле")
    @Test
    fun reservationWithWrongRoomType() {
        // Given
        val absentRoomType = RoomType.SEMI_LUX
        val roomReservationRequest = roomReservationRequest(hotelId = HotelsObjectMother.theHotel().id!!, roomType = absentRoomType)
        val guest = Guest.loginAsTheGuest()

        // When
        val errorResponse = guest.reservations.reserveRoomForError(roomReservationRequest, HttpStatus.CONFLICT)

        // Then
        errorResponse.status shouldBe  HttpStatus.CONFLICT.value()
        errorResponse.type.path shouldBe "room-type-not-found"
    }

    @DisplayName("должна не позволять бронировать номер, если в отеле нет свободных номеров заданного типа на весь запрошенный период")
    @Test
    fun reservationWithNoAvailableRooms() {
        // Given
        val roomType = RoomType.LUX
        val hotel = backgrounds.hotelBackgrounds.createHotel(rooms = { hotel ->
            listOf(
                RoomsObjectMother.room(hotel, roomType)
            )
        })
        val reservationFrom =  nearFutureDate(LocalDate.now())
        val reservationTo = reservationFrom.plusDays(1)
        val roomReservationRequest = roomReservationRequest(hotelId = hotel.id, roomType, from = reservationFrom, to = reservationTo)
        val guest = Guest.loginAsTheGuest()
        guest.reservations.reserveRoom(roomReservationRequest)

        // When
        val errorResponse = guest.reservations.reserveRoomForError(roomReservationRequest, expectedStatus = HttpStatus.CONFLICT)

        // Then
        errorResponse.status shouldBe  HttpStatus.CONFLICT.value()
        errorResponse.type.path shouldBe "no-available-rooms"
    }

    @DisplayName("должна возвращать 400 ошибку при запросе с пустым телом")
    @Test
    fun emptyRequestBody() {
        // Given
        val guest = Guest.loginAsTheGuest()

        // When
        val errorResponse = guest.reservations.reserveRoomForError("")

        // Then
        errorResponse.Then {
           statusCode(HttpStatus.BAD_REQUEST.value())
        }
    }

    @DisplayName("должна возвращать 400 ошибку при запросе без обязательного поля")
    @Test
    fun missingRequiredField() {
        // Given
        val guest = Guest.loginAsTheGuest()

        // When
        val errorResponse = guest.reservations.reserveRoomForError(createRoomReservationRequestJson(email = null))

        // Then
        errorResponse.Then {
            statusCode(HttpStatus.BAD_REQUEST.value())
        }
    }

    @DisplayName("должна возвращать 400 ошибку при запросе с неизвестным идентификатором типа номера")
    @Test
    fun unknownRoomType() {
        // Given
        val guest = Guest.loginAsTheGuest()

        // When
        val errorResponse = guest.reservations.reserveRoomForError(createRoomReservationRequestJson(roomTypeId = 3))

        // Then
        errorResponse.Then {
            statusCode(HttpStatus.BAD_REQUEST.value())
        }
    }

    @DisplayName("должна не позволять выполнять резервацию при запросе с датой до меньше, даты от")
    @Test
    fun toLessThanFrom() {
        // Given
        val reservationFrom = nearFutureDate(LocalDate.now())
        val reservationTo = reservationFrom.minusDays(1)
        val reserveRoomRequest = roomReservationRequest(from = reservationFrom, to = reservationTo)
        val guest = Guest.loginAsTheGuest()

        // When
        val errorResponse = guest.reservations.reserveRoomForError(reserveRoomRequest, HttpStatus.BAD_REQUEST)

        // Then
        errorResponse.status shouldBe  HttpStatus.BAD_REQUEST.value()
        errorResponse.type.path shouldBe "invalid-reservation-dates"
    }

    @DisplayName("должна не позволять выполнять резервацию начинающуюся ранее, чем завтра")
    @Test
    fun reservationInPast() {
        // Given
        val reservationFrom = LocalDate.now()
        val reservationTo = reservationFrom.plusDays(1)
        val reserveRoomRequest = roomReservationRequest(from = reservationFrom, to = reservationTo)
        val guest = Guest.loginAsTheGuest()

        // When
        val errorResponse = guest.reservations.reserveRoomForError(reserveRoomRequest, HttpStatus.BAD_REQUEST)

        // Then
        errorResponse.status shouldBe  HttpStatus.BAD_REQUEST.value()
        errorResponse.type.path shouldBe "reservation-dates-in-past"
    }
}