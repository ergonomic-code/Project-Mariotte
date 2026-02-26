package pro.azhidkov.mariotte.fixtures.clients.apis

import org.springframework.http.HttpStatus

/**
 * Результат HTTP-вызова в тестах: либо успех с телом, либо ожидаемая ошибка с телом.
 *
 * Используется в сценариях, где "успех/ошибка" определяется динамически (например, в конкурентных тестах).
 */
sealed interface HttpOutcome<out S, out E> {
    data class Success<S>(val body: S) : HttpOutcome<S, Nothing>
    data class ExpectedError<E>(val body: E) : HttpOutcome<Nothing, E>
}

data class ExpectedHttpError(
    val status: Int,
    val typePath: String?
)

/**
 * Утилита для компактного задания ожидаемой ошибки: `HttpStatus.CONFLICT with "no-available-rooms"`.
 */
infix fun HttpStatus.with(errorCode: String): ExpectedHttpError =
    ExpectedHttpError(status = this.value(), typePath = errorCode)

fun only(expectedHttpError: ExpectedHttpError): Set<ExpectedHttpError> =
    setOf(expectedHttpError)
