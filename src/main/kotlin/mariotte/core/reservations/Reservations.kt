package pro.azhidkov.mariotte.core.reservations

import pro.azhidkov.mariotte.core.hotels.root.Hotel
import pro.azhidkov.platform.domain.errors.DomainException
import java.time.LocalDate

sealed class ReservationRequestException(msg: String) : DomainException(msg)
class InvalidReservationDatesException(from: LocalDate, to: LocalDate) : ReservationRequestException("Reservation from greater than to: $from > $to")
class ReservationDatesInPastException(from: LocalDate) : ReservationRequestException("Reservation dates in past: $from")

object Reservations {

    fun reservationFromRequest(request: RoomReservationRequest, now: LocalDate): Result<Reservation> {
        if (request.from > request.to) {
            return Result.failure(InvalidReservationDatesException(request.from, request.to))
        }

        if (request.from <= now) {
            return Result.failure(ReservationDatesInPastException(request.from))
        }

        val reservation =
            Reservation(Hotel.ref(request.hotelId), request.roomType, request.email, request.from, request.to)
        
        return Result.success(reservation)
    }

}