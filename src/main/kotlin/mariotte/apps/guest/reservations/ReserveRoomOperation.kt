package pro.azhidkov.mariotte.apps.guest.reservations

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import pro.azhidkov.mariotte.core.hotels.HotelsService
import pro.azhidkov.mariotte.core.hotels.rooms.RoomType
import pro.azhidkov.mariotte.core.hotels.root.Hotel
import pro.azhidkov.mariotte.core.hotels.root.HotelRef
import pro.azhidkov.mariotte.core.reservations.Reservations
import pro.azhidkov.mariotte.core.reservations.ReservationsRepo
import pro.azhidkov.mariotte.core.reservations.RoomReservationRequest
import pro.azhidkov.mariotte.core.reservations.getActualReservations
import pro.azhidkov.platform.domain.errors.DomainException
import pro.azhidkov.platform.domain.errors.EntityNotFoundException
import java.time.LocalDate

data class ReservationSuccess(
    val reservationId: Int
)

class NoAvailableRoomsException(hotelRef: HotelRef, roomType: RoomType, from: LocalDate, to: LocalDate) :
    DomainException("There are no available rooms of type: $roomType in hotel $hotelRef from: $from to: $to")

@Component
class ReserveRoomOperation(
    private val reservationsRepo: ReservationsRepo,
    private val hotelsService: HotelsService
) : (RoomReservationRequest) -> ReservationSuccess {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    override operator fun invoke(roomReservationRequest: RoomReservationRequest): ReservationSuccess {
        log.info("Processing room reservation request: {}", roomReservationRequest)

        val reservation = Reservations.reservationFromRequest(roomReservationRequest, LocalDate.now())
            .onError { requestParsingFailed -> throw requestParsingFailed }

        val hotel = hotelsService.findById(roomReservationRequest.hotelId)
            .also { log.info("Hotel: {}", it) }
            ?: throw EntityNotFoundException(Hotel::class, roomReservationRequest.hotelId)

        val capacity = hotelsService.getCapacityForUpdate(hotel.ref(), reservation.roomType)
            .also { log.info("Capacity: {}", it) }
            .takeIf { it > 0 }
            ?: throw EntityNotFoundException(RoomType::class, reservation.roomType)

        val actualReservations =
            reservationsRepo.getActualReservations(hotel.ref(), reservation.roomType, reservation.from, reservation.to)
        log.info("Actual reservations: {}", actualReservations)
        if (!hasRequiredFreeRooms(actualReservations, capacity)) {
            throw NoAvailableRoomsException(hotel.ref(), reservation.roomType, reservation.from, reservation.to)
        }

        val persistedOrder = reservationsRepo.save(reservation)

        return ReservationSuccess(persistedOrder.id)
    }

}

private inline fun <T> Result<T>.onError(halt: (Throwable) -> Nothing): T {
    if (isFailure) {
        halt(exceptionOrNull()!!)
    }
    return getOrThrow()
}

private fun hasRequiredFreeRooms(actualReservations: Map<LocalDate, Int>, capacity: Int): Boolean {
    return actualReservations.all { it.value < capacity }
}