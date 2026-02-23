package pro.azhidkov.mariotte.platform.spring.test.concurrent

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch

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
