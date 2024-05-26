/**
 * Набор функции для генерации универасльных (не доменно специфичных) тестовых данных.
 *
 * Метки в коде:
 * 1. В качестве элементов данных, не имеющих значение в определённом тест-кейсе я использую случайные значения.
 *    Это, с одной стороны, упрощает генерацию тестовых объектов (потому что надо придумать и прописать меньшее
 *    количество значний), а, с другой стороны, повышает наглядность теста - в коде тест-кейса фигурируют только те
 *    данные, что имеют значение.
 *
 *    Однако такая практика может привести к flacky-тестам - когда (в зависимости от сгенерированного случайного
 *    значения) один и тот же тест, запущенный два раза подряд может показать разные результаты.
 *    Для того чтобы нивелировать эту проблему, я перед запуском каждого теста сбрасываю генераторы случайных чисел
 *    в предопределённое состояние.
 *
 *    @see MariotteBaseIntegrationTest
 */
package pro.azhidkov.mariotte.fixtures

import net.datafaker.Faker
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.random.Random

private const val SEED = 1L // 1

var random = Random(SEED)

var faker = Faker(Locale.of("ru"), java.util.Random(SEED))

/**
 * @see pro.azhidkov.mariotte.infra.spring.MariotteBaseIntegrationTest
 */
fun resetRandom() {
    random = Random(SEED)
    faker = Faker(Locale.of("ru"), java.util.Random(SEED))
}

fun randomIntId() = random.nextInt()

fun <T> Collection<T>.randomElement(): T {
    val idx = random.nextInt(0, this.size)
    return this.drop(idx).first()
}

const val NEAR_FUTURE_DAYS = 365L

fun nearFutureDate(
    after: LocalDate = LocalDate.now().plusDays(1),
    before: LocalDate = after.plusDays(NEAR_FUTURE_DAYS)
): LocalDate {
    val offset = random.nextLong(ChronoUnit.DAYS.between(after, before))
    return after.plusDays(offset)
}