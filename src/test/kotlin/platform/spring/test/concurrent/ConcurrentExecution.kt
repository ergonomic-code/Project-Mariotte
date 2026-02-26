package pro.azhidkov.mariotte.platform.spring.test.concurrent

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch

/**
 * Запускает [action] в нескольких потоках, синхронизируя старт, чтобы получить максимально "плотную" конкуренцию.
 *
 * Используется в тестах на конкурентные инварианты (например, защита от овербукинга).
 */
fun <T> executeSimultaneously(requestsCount: Int, action: () -> T): List<T> {
    val startLatch = CountDownLatch(1)
    val readyLatch = CountDownLatch(requestsCount)

    return (1..requestsCount)
        .map {
            CompletableFuture.supplyAsync {
                readyLatch.countDown()
                startLatch.await()
                action()
            }
        }
        .also { readyLatch.await() }
        .also { startLatch.countDown() }
        .map { it.get() }
}
