package pro.azhidkov.mariotte.cases.core.reservations

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import pro.azhidkov.mariotte.core.hotels.rooms.RoomType
import pro.azhidkov.mariotte.fixtures.ReservationsObjectMother
import pro.azhidkov.mariotte.fixtures.nearFutureDate
import pro.azhidkov.mariotte.core.reservations.ReservationsRepo
import pro.azhidkov.mariotte.core.reservations.getActualReservations
import pro.azhidkov.mariotte.fixtures.HotelsObjectMother
import pro.azhidkov.mariotte.fixtures.randomElement
import pro.azhidkov.mariotte.infra.spring.MariotteBaseTest


@DisplayName("Метод запроса загруженности отеля")
class FindRoomTypeReservationsPerDayTest(
    @Autowired val reservationsRepo: ReservationsRepo
) : MariotteBaseTest() {


    @DisplayName("должен учитывать резервацию, заканчивающуюся в первый день запрошенного интервала")
    @Test
    fun fromDateEdgeTest() {
        // Given
        val concurrentReservation = ReservationsObjectMother.concurrentReservations()

        val existingReservationFrom = nearFutureDate()
        val existingReservationTo = existingReservationFrom.plusDays(2)

        val newReservationFrom = existingReservationTo
        val newReservationTo = newReservationFrom.plusDays(2)

        val existingReservation = concurrentReservation(
            existingReservationFrom,
            existingReservationTo
        )
        reservationsRepo.save(existingReservation)

        val newReservation = concurrentReservation(
            newReservationFrom,
            newReservationTo
        )

        // When
        val actualReservations =
            reservationsRepo.getActualReservations(
                newReservation.hotelRef,
                newReservation.roomType,
                newReservation.from,
                newReservation.to
            )

        // Then
        actualReservations[newReservationFrom] shouldBe 1
    }


    @DisplayName("должен учитывать резервацию, начинающуюся в последний день запрошенного интервал")
    @Test
    fun toDateEdgeTest() {
        // Given
        val concurrentReservation = ReservationsObjectMother.concurrentReservations()

        val existingReservationFrom = nearFutureDate()
        val existingReservationTo = existingReservationFrom.plusDays(2)

        val newReservationFrom = existingReservationFrom.minusDays(2)
        val newReservationTo = existingReservationFrom

        val existingReservation = concurrentReservation(
            existingReservationFrom,
            existingReservationTo
        )
        reservationsRepo.save(existingReservation)

        val newReservation = concurrentReservation(
            newReservationFrom,
            newReservationTo
        )

        // When
        val actualReservations =
            reservationsRepo.getActualReservations(
                newReservation.hotelRef,
                newReservation.roomType,
                newReservation.from,
                newReservation.to
            )

        // Then
        actualReservations[newReservationTo] shouldBe 1
    }

    @DisplayName("должен учитывать резервацию, целиком содержащуюся в запрошенном интервале")
    @Test
    fun fullInclusionTest() {
        // Given
        val concurrentReservation = ReservationsObjectMother.concurrentReservations()

        val existingReservationFrom = nearFutureDate()
        val existingReservationTo = existingReservationFrom.plusDays(2)

        val newReservationFrom = existingReservationFrom.minusDays(2)
        val newReservationTo = existingReservationTo.plusDays(2)

        val existingReservation = concurrentReservation(
            existingReservationFrom,
            existingReservationTo
        )
        reservationsRepo.save(existingReservation)

        val newReservation = concurrentReservation(
            newReservationFrom,
            newReservationTo
        )

        // When
        val actualReservations =
            reservationsRepo.getActualReservations(newReservation.hotelRef, newReservation.roomType, newReservation.from, newReservation.to)

        // Then
        actualReservations[existingReservationFrom] shouldBe 1
        actualReservations[existingReservationTo] shouldBe 1
    }


    @DisplayName("должен учитывать резервацию, целиком содержащую запрошенный интервал")
    @Test
    fun partialInclusionTest() {
        // Given
        val concurrentReservation = ReservationsObjectMother.concurrentReservations()

        val existingReservationFrom = nearFutureDate()
        val existingReservationTo = existingReservationFrom.plusDays(8)

        val newReservationFrom = existingReservationFrom.plusDays(2)
        val newReservationTo = existingReservationTo.minusDays(2)

        val existingReservation = concurrentReservation(
            existingReservationFrom,
            existingReservationTo
        )
        reservationsRepo.save(existingReservation)

        val newReservation = concurrentReservation(
            newReservationFrom,
            newReservationTo
        )

        // When
        val actualReservations =
            reservationsRepo.getActualReservations(newReservation.hotelRef, newReservation.roomType, newReservation.from, newReservation.to)

        // Then
        actualReservations[newReservationFrom] shouldBe 1
        actualReservations[newReservationTo] shouldBe 1
    }

    @DisplayName("должен корректно учитывать наложение существующих резерваций")
    @Test
    fun reservationsOverlappingTest() {
        // Given
        val hotelRef = HotelsObjectMother.theHotel()
        val roomType = RoomType.entries.randomElement()
        val concurrentReservation = ReservationsObjectMother.concurrentReservations(hotelRef, roomType)

        val existingReservation1From = nearFutureDate()
        val existingReservation1To = existingReservation1From.plusDays(1)

        val existingReservation2From = existingReservation1To
        val existingReservation2To = existingReservation2From.plusDays(1)

        reservationsRepo.save(concurrentReservation(existingReservation1From, existingReservation1To))
        reservationsRepo.save(concurrentReservation(existingReservation2From, existingReservation2To))

        // When
        val actualReservations =
            reservationsRepo.getActualReservations(hotelRef, roomType, existingReservation1From, existingReservation2To)

        // Then
        actualReservations[existingReservation1From] shouldBe 1
        actualReservations[existingReservation1To] shouldBe 2
        actualReservations[existingReservation2To] shouldBe 1
    }

    @DisplayName("должен учитывать только резервации номеров запрошенного типа")
    @Test
    fun roomTypeSelection() {
        // Given
        val hotelRef = HotelsObjectMother.theHotel()
        val luxRoomType = RoomType.LUX

        val reservationFrom = nearFutureDate()
        val reservationTo = reservationFrom.plusDays(1)

        val luxReservation = ReservationsObjectMother.reservation(hotelRef, luxRoomType, from = reservationFrom, to = reservationTo)
        val semiLuxReservation =
            ReservationsObjectMother.reservation(hotelRef, RoomType.SEMI_LUX, from = reservationFrom, to = reservationTo)

        reservationsRepo.save(luxReservation)
        reservationsRepo.save(semiLuxReservation)

        // When
        val actualReservations =
            reservationsRepo.getActualReservations(hotelRef, luxRoomType, reservationFrom, reservationTo)

        // Then
        actualReservations[reservationFrom] shouldBe 1
        actualReservations[reservationTo] shouldBe 1
    }

}