package pro.azhidkov.mariotte.cases.app.reservations

import io.kotest.inspectors.forExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import pro.azhidkov.mariotte.app.reservations.NoAvailableRoomsException
import pro.azhidkov.mariotte.assertions.shouldMatch
import pro.azhidkov.mariotte.cases.app.platform.annotations.MariotteApiTest
import pro.azhidkov.mariotte.domain.reservations.ReservationPeriod
import pro.azhidkov.mariotte.domain.rooms.RoomType
import pro.azhidkov.mariotte.fixtures.clients.ApiSessionsFactory
import pro.azhidkov.mariotte.fixtures.clients.apis.HttpOutcome
import pro.azhidkov.mariotte.fixtures.clients.apis.only
import pro.azhidkov.mariotte.fixtures.clients.apis.reservationsApi
import pro.azhidkov.mariotte.fixtures.clients.apis.with
import pro.azhidkov.mariotte.fixtures.object_mothers.*
import pro.azhidkov.mariotte.fixtures.presets.HotelsFixturePresets
import pro.azhidkov.mariotte.fixtures.presets.NewHotelFixture
import pro.azhidkov.mariotte.platform.spring.test.concurrent.executeSimultaneously
import java.time.LocalDate
import java.time.Period

/**
 * Тест-кейсы на операцию бронирования номера.
 *
 * Метки в коде
 * 1. Этот тест будет мигать, если его запускать в районе 23:59:59, но во имя простоты демо, на это можно пойти
 */
@MariotteApiTest
@DisplayName("Операция API - Бронирование номера")
class ReserveRoomApiOpTest(
    @param:Autowired private val hotelsFixturePresets: HotelsFixturePresets,
    @param:Autowired private val apiSessionsFactory: ApiSessionsFactory
) {

    @Test
    fun `должна возвращать идентификатор брони, по которому можно получить детали брони`() {
        // Given
        val roomType = RoomType.LUX
        val reserveRoomRequest =
            ReservationsObjectMother.aRoomReservationRequest(
                hotelId = HotelsObjectMother.theHotel.ref.id,
                roomType = roomType
            )
        val guest = apiSessionsFactory.aGuest

        // When
        val reservationSuccess = guest.reservationsApi.reserveRoom(reserveRoomRequest)

        // And when
        val reservation = guest.reservationsApi.getReservation(reservationSuccess.reservationId)

        // Then
        reservation shouldMatch reserveRoomRequest
    }

    @Test
    fun `должна не позволять бронировать номера по несуществующему идентификатору отеля`() {
        // Given
        val notExistingHotelId = faker.randomUuid()
        val roomReservationRequest = ReservationsObjectMother.aRoomReservationRequest(hotelId = notExistingHotelId)
        val guest = apiSessionsFactory.aGuest

        // When
        val errorResponse = guest.reservationsApi.reserveRoomForError(roomReservationRequest)

        // Then
        errorResponse.status shouldBe HttpStatus.NOT_FOUND.value()
        errorResponse.type?.path shouldBe "hotel-not-found"
    }

    @Test
    fun `должна не позволять бронировать номера типа, не представленного в отеле`() {
        // Given
        val absentRoomType = RoomType.SEMI_LUX
        val roomReservationRequest =
            ReservationsObjectMother.aRoomReservationRequest(
                hotelId = HotelsObjectMother.theHotel.ref.id,
                roomType = absentRoomType
            )
        val guest = apiSessionsFactory.aGuest

        // When
        val errorResponse = guest.reservationsApi.reserveRoomForError(roomReservationRequest)

        // Then
        errorResponse.status shouldBe HttpStatus.NOT_FOUND.value()
        errorResponse.type?.path shouldBe "room-type-not-found"
    }

    @Test
    fun `должна возвращать 404 при запросе деталей несуществующей брони`() {
        // Given
        val reservationId = faker.randomUuid()
        val guest = apiSessionsFactory.aGuest

        // When
        val errorResponse = guest.reservationsApi.getReservationForError(reservationId)

        // Then
        errorResponse.status shouldBe HttpStatus.NOT_FOUND.value()
        errorResponse.type?.path shouldBe "reservation-not-found"
    }

    @Test
    fun `должна не позволять бронировать номер, если в отеле нет свободных номеров заданного типа на весь запрошенный период`() {
        // Given
        val luxRoomType = RoomType.LUX
        val hotelWithSingleLux = hotelsFixturePresets
            .insertFixture(NewHotelFixture.ofHotelWithSingleRoom(luxRoomType))
            .hotel
        val reservationFrom = nearFutureDate(LocalDate.now())
        val period = ReservationPeriod(Period.ofDays(1))
        val roomReservationRequest =
            ReservationsObjectMother.aRoomReservationRequest(
                hotelId = hotelWithSingleLux.id,
                luxRoomType,
                from = reservationFrom,
                period = period
            )
        val guest = apiSessionsFactory.aGuest

        // When
        guest.reservationsApi.reserveRoom(roomReservationRequest)

        // And when
        val errorResponse =
            guest.reservationsApi.reserveRoomForError(roomReservationRequest)

        // Then
        errorResponse.status shouldBe HttpStatus.CONFLICT.value()
        errorResponse.type?.path shouldBe "no-available-rooms"
    }

    @Test
    fun `должна возвращать 400 ошибку при запросе с пустым телом`() {
        // Given
        val guest = apiSessionsFactory.aGuest

        // When
        val errorResponse = guest.reservationsApi.reserveRoomForError("")

        // Then
        errorResponse.status shouldBe HttpStatus.BAD_REQUEST.value()
    }

    @Test
    fun `должна возвращать 400 ошибку при запросе без обязательного поля`() {
        // Given
        val guest = apiSessionsFactory.aGuest

        // When
        val errorResponse = guest.reservationsApi.reserveRoomForError(
            ReservationsObjectMother.aRoomReservationRequestJson(
                email = null
            )
        )

        // Then
        errorResponse.status shouldBe HttpStatus.BAD_REQUEST.value()
    }

    @Test
    fun `должна возвращать 400 ошибку при запросе с неизвестным идентификатором типа номера`() {
        // Given
        val notExistingRoomTypeId = 3
        val guest = apiSessionsFactory.aGuest

        // When
        val errorResponse =
            guest.reservationsApi.reserveRoomForError(ReservationsObjectMother.aRoomReservationRequestJson(roomTypeId = notExistingRoomTypeId))

        // Then
        errorResponse.status shouldBe HttpStatus.BAD_REQUEST.value()
    }

    @Test
    fun `должна не позволять выполнять бронь начинающуюся ранее, чем завтра`() {
        // Given
        val today = LocalDate.now()
        val reserveRoomRequest = ReservationsObjectMother.aRoomReservationRequest(from = today)
        val guest = apiSessionsFactory.aGuest

        // When
        val errorResponse = guest.reservationsApi.reserveRoomForError(reserveRoomRequest)

        // Then
        errorResponse.status shouldBe HttpStatus.UNPROCESSABLE_ENTITY.value()
        errorResponse.type?.path shouldBe "reservation-dates-in-past"
    }

    @RepeatedTest(value = 10, failureThreshold = 1)
    fun `должна исключать овербукинг при конкурентных запросах на бронирование`() {
        // Given
        val roomType = RoomType.LUX
        val capacity = HotelsObjectMother.theHotel.capacity.getValue(roomType)
        val requests = capacity * 2
        val reserveRoomRequest = ReservationsObjectMother.aRoomReservationRequest(
            hotelId = HotelsObjectMother.theHotel.ref.id,
            roomType = roomType,
            from = nearFutureDate(LocalDate.now()),
        )
        val guest = apiSessionsFactory.aGuest

        // When
        val reservationResults = executeSimultaneously(requests) {
            guest.reservationsApi.reserveRoomForOutcome(
                roomReservationRq = reserveRoomRequest,
                expectedErrors = only(HttpStatus.CONFLICT with NoAvailableRoomsException.CODE)
            )
        }

        // Then
        reservationResults.forExactly(capacity) { it.shouldBeInstanceOf<HttpOutcome.Success<*>>() }
        reservationResults.forExactly(requests - capacity) { it.shouldBeInstanceOf<HttpOutcome.ExpectedError<*>>() }
    }

    @Test
    fun `должна не позволять выполнять резервацию при запросе с длительностью менее 1 дня`() {
        // Given
        val reservationFrom = nearFutureDate(LocalDate.now())
        val reservationPeriod = Period.ZERO
        val bodyJson = ReservationsObjectMother.aRoomReservationRequestJson(
            email = "",
            from = reservationFrom,
            period = reservationPeriod
        )
        val guest = apiSessionsFactory.aGuest

        // When
        val errorResponse = guest.reservationsApi.reserveRoomForError(bodyJson)

        // Then
        errorResponse.status shouldBe HttpStatus.BAD_REQUEST.value()
    }

}
