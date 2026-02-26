package pro.azhidkov.mariotte.domain.reservations

import java.time.Period
import java.time.chrono.ChronoPeriod


/**
 * Объект-значение, представляющий период бронирования номера.
 * Не может быть длиной 0 дней.
 */
@JvmInline
value class ReservationPeriod(
    val period: Period
) : ChronoPeriod by period {

    val days: UInt
        get() = period.days.toUInt()

    override fun isZero(): Boolean {
        return super.isZero()
    }

    override fun isNegative(): Boolean {
        return super.isNegative()
    }

    override fun negated(): ChronoPeriod? {
        return super.negated()
    }

    init {
        require(period.days > 0) { "Reservation period should more than 0 days" }
    }

}
