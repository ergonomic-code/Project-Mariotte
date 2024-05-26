package pro.azhidkov.mariotte.cases.apps.guest.reservation

import io.kotest.inspectors.forExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import pro.azhidkov.mariotte.apps.guest.reservations.ReservationSuccess
import pro.azhidkov.mariotte.assertions.shouldMatch
import pro.azhidkov.mariotte.backgrounds.Backgrounds
import pro.azhidkov.mariotte.clients.Guest
import pro.azhidkov.mariotte.core.hotels.rooms.RoomType
import pro.azhidkov.mariotte.core.reservations.ReservationPeriod
import pro.azhidkov.mariotte.fixtures.HotelsObjectMother
import pro.azhidkov.mariotte.fixtures.ReservationsObjectMother.createRoomReservationRequestJson
import pro.azhidkov.mariotte.fixtures.ReservationsObjectMother.roomReservationRequest
import pro.azhidkov.mariotte.fixtures.RoomsObjectMother
import pro.azhidkov.mariotte.fixtures.nearFutureDate
import pro.azhidkov.mariotte.fixtures.randomElement
import pro.azhidkov.mariotte.infra.spring.MariotteBaseIntegrationTest
import java.time.LocalDate
import java.time.Period
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors


/**
 * Тест-кейсы на операцию бронирования номера.
 *
 * Метки в коде
 * 1. Этот тест будет мигать, если его запускать в районе 23:59:59, но во имя простоты демо, на этой можно пойти
 */
@DisplayName("Операция - Бронирование номера")
class RoomReservationTest(
    @Autowired private val backgrounds: Backgrounds
) : MariotteBaseIntegrationTest() {

    @DisplayName("должно возвращать идентификатор брони, по которому можно получить детали брони")
    @Test
    fun reservationPersistenceTest() {
        // Given
        val roomType = RoomType.LUX
        val reserveRoomRequest =
            roomReservationRequest(hotelId = HotelsObjectMother.theHotel.ref.id!!, roomType = roomType)
        val guest = Guest.loginAsTheGuest(client)

        // When
        val reservationSuccess = guest.reservations.reserveRoom(reserveRoomRequest)

        // And when
        val reservation = guest.reservations.getReservation(reservationSuccess.reservationId)

        // Then
        reservation shouldMatch reserveRoomRequest
    }

    @DisplayName("должно не позволять бронировать номера по несуществующему идентификатору отеля")
    @Test
    fun reservationInNotExistingHotel() {
        // Given
        val notExistingHotelId = 404
        val roomReservationRequest = roomReservationRequest(hotelId = notExistingHotelId)
        val guest = Guest.loginAsTheGuest(client)

        // When
        val errorResponse = guest.reservations.reserveRoomForError(roomReservationRequest)

        // Then
        errorResponse.status shouldBe HttpStatus.CONFLICT.value()
        errorResponse.type.path shouldBe "hotel-not-found"
    }

    @DisplayName("должно не позволять бронировать номера типа, не представленного в отеле")
    @Test
    fun reservationWithWrongRoomType() {
        // Given
        val absentRoomType = RoomType.SEMI_LUX
        val roomReservationRequest =
            roomReservationRequest(hotelId = HotelsObjectMother.theHotel.ref.id!!, roomType = absentRoomType)
        val guest = Guest.loginAsTheGuest(client)

        // When
        val errorResponse = guest.reservations.reserveRoomForError(roomReservationRequest)

        // Then
        errorResponse.status shouldBe HttpStatus.CONFLICT.value()
        errorResponse.type.path shouldBe "room-type-not-found"
    }

    @DisplayName("должно не позволять бронировать номер, если в отеле нет свободных номеров заданного типа на весь запрошенный период")
    @Test
    fun reservationWithNoAvailableRooms() {
        // Given
        val luxRoomType = RoomType.LUX
        val hotelWithSingleLux = backgrounds.hotelBackgrounds.createHotel(rooms = { hotel ->
            listOf(
                RoomsObjectMother.room(hotel, luxRoomType)
            )
        })
        val reservationFrom = nearFutureDate(LocalDate.now())
        val period = ReservationPeriod(Period.ofDays(1))
        val roomReservationRequest =
            roomReservationRequest(
                hotelId = hotelWithSingleLux.id,
                luxRoomType,
                from = reservationFrom,
                period = period
            )
        val guest = Guest.loginAsTheGuest(client)

        // When
        guest.reservations.reserveRoom(roomReservationRequest)

        // And when
        val errorResponse =
            guest.reservations.reserveRoomForError(roomReservationRequest)

        // Then
        errorResponse.status shouldBe HttpStatus.CONFLICT.value()
        errorResponse.type.path shouldBe "no-available-rooms"
    }

    @DisplayName("должно возвращать 400 ошибку при запросе с пустым телом")
    @Test
    fun emptyRequestBody() {
        // Given
        val guest = Guest.loginAsTheGuest(client)

        // When
        val errorResponse = guest.reservations.reserveRoomForError("")

        // Then
        errorResponse.status shouldBe HttpStatus.BAD_REQUEST.value()
    }

    @DisplayName("должно возвращать 400 ошибку при запросе без обязательного поля")
    @Test
    fun missingRequiredField() {
        // Given
        val guest = Guest.loginAsTheGuest(client)

        // When
        val errorResponse = guest.reservations.reserveRoomForError(createRoomReservationRequestJson(email = null))

        // Then
        errorResponse.status shouldBe HttpStatus.BAD_REQUEST.value()
    }

    @DisplayName("должно возвращать 400 ошибку при запросе с неизвестным идентификатором типа номера")
    @Test
    fun unknownRoomType() {
        // Given
        val notExistingRoomTypeId = 3
        val guest = Guest.loginAsTheGuest(client)

        // When
        val errorResponse =
            guest.reservations.reserveRoomForError(createRoomReservationRequestJson(roomTypeId = notExistingRoomTypeId))

        // Then
        errorResponse.status shouldBe HttpStatus.BAD_REQUEST.value()
    }

    @DisplayName("должно не позволять выполнять резервацию начинающуюся ранее, чем завтра")
    @Test
    fun reservationInPast() {
        // Given
        val today = LocalDate.now()
        val reserveRoomRequest = roomReservationRequest(from = today)
        val guest = Guest.loginAsTheGuest(client)

        // When
        val errorResponse = guest.reservations.reserveRoomForError(reserveRoomRequest)

        // Then
        errorResponse.status shouldBe HttpStatus.CONFLICT.value()
        errorResponse.type.path shouldBe "reservation-dates-in-past"
    }

    private val threadPool = Executors.newFixedThreadPool(10)

    @DisplayName("должно исключать бронирование большего количества номеров за одни сутки, чем есть в отеле")
    @RepeatedTest(value = 10, failureThreshold = 1)
    fun concurrentReservation() {
        // Given
        val roomType = HotelsObjectMother.theHotel.availableRoomTypes().toList().randomElement()
        val capacity = HotelsObjectMother.theHotel.capacity.getValue(roomType)
        val reservationFrom = nearFutureDate(LocalDate.now())
        val reserveRoomRequest = roomReservationRequest(
            hotelId = HotelsObjectMother.theHotel.ref.id!!,
            roomType = roomType,
            from = reservationFrom,
        )
        val guest = Guest.loginAsTheGuest(client)

        // When
        val reservationResults = (1..10)
            .map { CompletableFuture.supplyAsync({ guest.reservations.reserveRoom(reserveRoomRequest) }, threadPool) }
            .map { it.handle { res, _ -> res }.get() }

        // Then
        reservationResults.forExactly(capacity) { it.shouldBeInstanceOf<ReservationSuccess>() }
    }


    @DisplayName("должна не позволять выполнять резервацию при запросе с длительностью менее 1 дня")
    @Test
    fun toLessThanFrom() {
        // Given
        val reservationFrom = nearFutureDate(LocalDate.now())
        val reservationPeriod = Period.ZERO
        val bodyJson = createRoomReservationRequestJson(email = "", from = reservationFrom, period = reservationPeriod)
        val guest = Guest.loginAsTheGuest(client)

        // When
        val errorResponse = guest.reservations.reserveRoomForError(bodyJson)

        // Then
        errorResponse.status shouldBe HttpStatus.BAD_REQUEST.value()
    }

}