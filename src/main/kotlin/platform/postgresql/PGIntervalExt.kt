package pro.azhidkov.platform.postgresql

import org.postgresql.util.PGInterval
import java.time.Duration
import java.time.Period

fun PGInterval.toPeriod(): Period = Period.ofDays(this.days)

fun Period.toPGInterval() = PGInterval(this.toString())