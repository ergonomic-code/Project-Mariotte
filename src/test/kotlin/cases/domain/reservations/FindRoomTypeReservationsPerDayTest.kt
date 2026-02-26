package pro.azhidkov.mariotte.cases.domain.reservations

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import pro.azhidkov.mariotte.cases.domain.platform.annotations.MariotteSdjTest
import pro.azhidkov.mariotte.domain.rooms.RoomType
import pro.azhidkov.mariotte.domain.reservations.ReservationPeriod
import pro.azhidkov.mariotte.domain.reservations.ReservationsRepo
import pro.azhidkov.mariotte.domain.reservations.to
import pro.azhidkov.mariotte.fixtures.object_mothers.HotelsObjectMother
import pro.azhidkov.mariotte.fixtures.object_mothers.ReservationsObjectMother
import pro.azhidkov.mariotte.fixtures.object_mothers.nearFutureDate
import java.time.Period

/**
 * Тест-кейсы на операцию ресурса по поиску количества резерваций определённого типа по дням определённого периода.
 *
 * В целом по Эргономичному подходу стоит отдавать предпочтение верхнеуровневым тестам, работающим через публичное API
 * системы.
 * Однако в случае сложных методов допускается тестирование этих методов напрямую.
 * И в данном случае реализация метода содержит множество граничных условий, которые надо проверить для того, чтобы спать
 * спокойно, и которые существенно проще проверить напрямую.
 */
@MariotteSdjTest
@Import(ReservationsRepo::class)
@DisplayName("Ресурс - репозиторий броней - метод запроса загруженности отеля")
class FindRoomTypeReservationsPerDayTest(
    @param:Autowired private val reservationsRepo: ReservationsRepo
) {

    @Test
    fun `должен учитывать резервацию, заканчивающуюся в первый день запрошенного интервала`() {
        // Given
        val concurrentReservation = ReservationsObjectMother.concurrentReservations()

        val existingReservationFrom = nearFutureDate()
        val existingReservationPeriod = ReservationPeriod(Period.ofDays(2))

        val newReservationFrom = existingReservationFrom.plus(existingReservationPeriod)
        val newReservationPeriod = ReservationPeriod(Period.ofDays(2))

        val existingReservation = concurrentReservation(
            existingReservationFrom,
            existingReservationPeriod
        )
        reservationsRepo.save(existingReservation)

        val newReservation = concurrentReservation(
            newReservationFrom,
            newReservationPeriod
        )

        // When
        val actualReservations =
            reservationsRepo.getReservationsAmountPerDate(
                newReservation.hotel,
                newReservation.roomType,
                newReservation.from,
                newReservation.to
            )

        // Then
        actualReservations[newReservationFrom] shouldBe 1
    }

    @Test
    fun `должен учитывать резервацию, начинающуюся в последний день запрошенного интервала`() {
        // Given
        val concurrentReservation = ReservationsObjectMother.concurrentReservations()

        val newReservationFrom = nearFutureDate()
        val newReservationPeriod = ReservationPeriod(Period.ofDays(2))

        val newReservation = concurrentReservation(
            newReservationFrom,
            newReservationPeriod
        )

        val existingReservationFrom = newReservationFrom.plus(newReservationPeriod)

        val existingReservation = concurrentReservation(
            existingReservationFrom,
            ReservationPeriod(Period.ofDays(1))
        )
        reservationsRepo.save(existingReservation)

        // When
        val actualReservations =
            reservationsRepo.getReservationsAmountPerDate(
                newReservation.hotel,
                newReservation.roomType,
                newReservation.from,
                newReservation.to
            )

        // Then
        actualReservations[newReservation.to] shouldBe 1
    }

    @Test
    fun `должен учитывать резервацию, целиком содержащуюся в запрошенном интервале`() {
        // Given
        val concurrentReservation = ReservationsObjectMother.concurrentReservations()

        val existingReservationFrom = nearFutureDate()
        val existingReservationPeriod = ReservationPeriod(Period.ofDays(2))

        val newReservationFrom = existingReservationFrom.minusDays(2)
        val newReservationPeriod = ReservationPeriod(Period.ofDays(existingReservationPeriod.days.toInt() * 2))

        val existingReservation = concurrentReservation(
            existingReservationFrom,
            existingReservationPeriod
        )
        reservationsRepo.save(existingReservation)

        val newReservation = concurrentReservation(
            newReservationFrom,
            newReservationPeriod
        )

        // When
        val actualReservations =
            reservationsRepo.getReservationsAmountPerDate(
                newReservation.hotel,
                newReservation.roomType,
                newReservation.from,
                newReservation.to
            )

        // Then
        actualReservations[existingReservationFrom] shouldBe 1
        actualReservations[existingReservation.to] shouldBe 1
    }

    @Test
    fun `должен учитывать резервацию, целиком содержащую запрошенный интервал`() {
        // Given
        val concurrentReservation = ReservationsObjectMother.concurrentReservations()

        val existingReservationFrom = nearFutureDate()
        val existingReservationPeriod = ReservationPeriod(Period.ofDays(8))

        val newReservationFrom = existingReservationFrom.plusDays(2)
        val newReservationPeriod = ReservationPeriod(Period.ofDays(2))

        val existingReservation = concurrentReservation(
            existingReservationFrom,
            existingReservationPeriod
        )
        reservationsRepo.save(existingReservation)

        val newReservation = concurrentReservation(
            newReservationFrom,
            newReservationPeriod
        )

        // When
        val actualReservations =
            reservationsRepo.getReservationsAmountPerDate(
                newReservation.hotel,
                newReservation.roomType,
                newReservation.from,
                newReservation.to
            )

        // Then
        actualReservations[newReservationFrom] shouldBe 1
        actualReservations[newReservation.to] shouldBe 1
    }

    @Test
    fun `должен корректно учитывать наложение существующих резерваций`() {
        // Given
        val hotel = HotelsObjectMother.theHotel.ref
        val roomType = RoomType.LUX
        val concurrentReservation = ReservationsObjectMother.concurrentReservations(hotel, roomType)

        val existingReservation1From = nearFutureDate()
        val existingReservation1Period = ReservationPeriod(Period.ofDays(1))

        val existingReservation2From = existingReservation1From.plus(existingReservation1Period)
        val existingReservation2Period = ReservationPeriod(Period.ofDays(1))

        reservationsRepo.save(concurrentReservation(existingReservation1From, existingReservation1Period))
        reservationsRepo.save(concurrentReservation(existingReservation2From, existingReservation2Period))

        // When
        val actualReservations =
            reservationsRepo.getReservationsAmountPerDate(
                hotel,
                roomType,
                existingReservation1From,
                existingReservation2From.plus(existingReservation2Period)
            )

        // Then
        actualReservations[existingReservation1From] shouldBe 1
        actualReservations[existingReservation1From.plus(existingReservation1Period)] shouldBe 2
        actualReservations[existingReservation2From.plus(existingReservation2Period)] shouldBe 1
    }

    @Test
    fun `должен учитывать только резервации номеров запрошенного типа`() {
        // Given
        val hotel = HotelsObjectMother.theHotel.ref
        val luxRoomType = RoomType.LUX

        val reservationFrom = nearFutureDate()
        val reservationPeriod = ReservationPeriod(Period.ofDays(1))

        val luxReservation =
            ReservationsObjectMother.aReservation(
                hotel,
                luxRoomType,
                from = reservationFrom,
                period = reservationPeriod
            )
        val semiLuxReservation =
            ReservationsObjectMother.aReservation(
                hotel,
                RoomType.SEMI_LUX,
                from = reservationFrom,
                period = reservationPeriod
            )

        reservationsRepo.save(luxReservation)
        reservationsRepo.save(semiLuxReservation)

        // When
        val actualReservations =
            reservationsRepo.getReservationsAmountPerDate(
                hotel,
                luxRoomType,
                reservationFrom,
                reservationFrom.plus(reservationPeriod)
            )

        // Then
        actualReservations[reservationFrom] shouldBe 1
        actualReservations[reservationFrom.plus(reservationPeriod)] shouldBe 1
    }

}
