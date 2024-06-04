package pro.azhidkov.mariotte.core.reservations

import java.time.Period
import java.time.chrono.ChronoPeriod


/**
 * Объект-значения представляющий период бронирования номера.
 * Не может быть длиной 0 дней.
 *
 * * Слой в Функциональной архитектуре: чистое ядро
 * * Тип блока в структурном дизайне: н/а
 * * Слой в чистой архитектуре: сущности
 * * Тип блока в Эргономичной архитектуре: запись ресурса
 */
@JvmInline
value class ReservationPeriod(
    val period: Period
) : ChronoPeriod by period {

    val days: UInt
        get() = period.days.toUInt()

    init {
        require(period.days > 0) { "Reservation period should more than 0 days" }
    }

}