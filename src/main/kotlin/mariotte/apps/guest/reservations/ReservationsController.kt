package pro.azhidkov.mariotte.apps.guest.reservations


import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pro.azhidkov.mariotte.apps.platform.spring.http.badRequestOf
import pro.azhidkov.mariotte.apps.platform.spring.http.conflictOf
import pro.azhidkov.mariotte.core.reservations.Reservation
import pro.azhidkov.mariotte.core.reservations.ReservationRequestException
import pro.azhidkov.mariotte.core.reservations.ReservationsRepo
import pro.azhidkov.mariotte.core.reservations.RoomReservationRequest
import pro.azhidkov.platform.domain.errors.EntityNotFoundException
import pro.azhidkov.platform.kotlin.mapFailure
import pro.azhidkov.platform.kotlin.mapSuccess

@RestController
@RequestMapping("/guest/reservations")
class ReservationsController(
    private val reserveRoom: ReserveRoomOperation,
    private val reservationsRepo: ReservationsRepo
) {

    @PostMapping
    fun handleReserveRoom(@RequestBody request: RoomReservationRequest): ResponseEntity<*> {
        val res: Result<ReservationSuccess> = runCatching { reserveRoom((request)) }

        return res
            .mapSuccess { ResponseEntity.ok(it) }
            .mapFailure<ReservationRequestException, _, _> { badRequestOf(it) }
            .mapFailure<EntityNotFoundException, _, _> { conflictOf(it) }
            .mapFailure<NoAvailableRoomsException, _, _> { conflictOf(it) }
            .getOrThrow()
    }

    @GetMapping("/{reservationId}")
    fun handleGetReservation(@PathVariable reservationId: Int): ResponseEntity<Reservation> {
        val res = reservationsRepo.findByIdOrNull(reservationId)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(res)
    }

}