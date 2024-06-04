package pro.azhidkov.mariotte.fixtures

import net.datafaker.Faker
import pro.azhidkov.mariotte.core.reservations.ReservationPeriod
import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.random.Random


private const val SEED = 1

var random = Random(SEED)

var faker = Faker(Locale.of("ru"), java.util.Random(1))

fun resetRandom() {
    random = Random(SEED)
    faker = Faker(Locale.of("ru"), java.util.Random(1))
}

fun randomIntId() = random.nextInt()

fun <T> Collection<T>.randomElement(): T {
    val idx = random.nextInt(0, this.size)
    return this.drop(idx).first()
}

const val NEAR_FUTURE_DAYS = 365L

fun nearFutureDate(
    after: LocalDate = LocalDate.now(),
    before: LocalDate = after.plusDays(NEAR_FUTURE_DAYS)
): LocalDate {
    val offset = random.nextLong(ChronoUnit.DAYS.between(after, before))
    return after.plusDays(offset)
}

fun randomReservationPeriod(): ReservationPeriod =
    ReservationPeriod(Period.ofDays(random.nextInt(1, 14)))