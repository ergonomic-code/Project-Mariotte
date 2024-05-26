package pro.azhidkov.mariotte.apps.guest.reservations

import pro.azhidkov.mariotte.core.reservations.Reservation
import java.time.LocalDate


/**
 * Модуль (набор функций) бизнес-правил операции бронирования.
 * * Слой в Функциональной архитектуре: чистое ядро
 * * Тип блока в Структурном дизайне: трансформации
 * * Слой в Чистой архитектуре: сущности/домен
 * * Тип блока в Эргономичной архитектуре: операции
 */
object ReservationRules {

    /**
     * Бизнес-правило, ограниченное одним ресурсом - "Бронирования".
     */
    fun canAcceptAt(reservation: Reservation, reservationRequestDate: LocalDate) =
        reservation.from.isAfter(reservationRequestDate)

    /**
     * Кросс-ресурсное бизнес-правило - для того, чтобы понять, есть ли у нас достаточное количество свободных
     * комнат, нам надо посмотреть и на ресурс "Отель" и на ресурс "Бронирования".
     */
    fun hasRequiredFreeRooms(capacity: Int, actualReservations: Map<LocalDate, Int>): Boolean {
        return actualReservations.all { it.value < capacity }
    }

}