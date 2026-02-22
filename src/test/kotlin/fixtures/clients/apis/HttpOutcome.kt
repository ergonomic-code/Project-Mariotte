package pro.azhidkov.mariotte.fixtures.clients.apis

import org.springframework.http.HttpStatus

sealed interface HttpOutcome<out S, out E> {
    data class Success<S>(val body: S) : HttpOutcome<S, Nothing>
    data class ExpectedError<E>(val body: E) : HttpOutcome<Nothing, E>
}

data class ExpectedHttpError(
    val status: Int,
    val typePath: String?
)

infix fun HttpStatus.with(errorCode: String): ExpectedHttpError =
    ExpectedHttpError(status = this.value(), typePath = errorCode)

fun only(expectedHttpError: ExpectedHttpError): Set<ExpectedHttpError> =
    setOf(expectedHttpError)
