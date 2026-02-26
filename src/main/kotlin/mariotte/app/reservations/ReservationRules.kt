package pro.azhidkov.mariotte.app.reservations

import pro.azhidkov.mariotte.domain.reservations.Reservation
import java.time.LocalDate


/**
 * Набор бизнес-правил операции бронирования.
 */
object ReservationRules {

    /**
     * Бизнес-правило, ограниченное ресурсом "Бронирования".
     */
    fun canAcceptAt(reservation: Reservation, reservationRequestDate: LocalDate) =
        reservation.from.isAfter(reservationRequestDate)

    /**
     * Кросс-ресурсное бизнес-правило: чтобы понять, хватает ли свободных комнат,
     * нужно учесть и вместимость отеля, и уже существующие бронирования.
     */
    fun hasRequiredFreeRooms(capacity: Int, actualReservations: Map<LocalDate, Int>): Boolean {
        return actualReservations.all { it.value < capacity }
    }

}
