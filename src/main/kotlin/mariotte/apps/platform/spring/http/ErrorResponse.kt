package pro.azhidkov.mariotte.apps.platform.spring.http

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import pro.azhidkov.platform.domain.errors.DomainException
import java.net.URI
import java.time.Instant

/**
 * Небольшой адаптер для ProblemDetail, добавляющий timestamp к объекту деталей, конструктор, позволяющий
 * сразу полностью проинициализировать объект и несколько утилитных методов.
 */
class ErrorResponse(
    instance: URI?,
    status: Int,
    type: URI,
    title: String,
    detail: String,
    timestamp: Instant = Instant.now()
) : ProblemDetail() {

    constructor(cause: DomainException, status: HttpStatus) : this(
        null,
        status = status.value(),
        type = URI.create(cause.errorCode),
        title = status.name,
        detail = cause.message ?: status.name
    )

    init {
        check(status in 100..599) { "Invalid HTTP status code: $status" }

        super.setStatus(status)
        super.setInstance(instance)
        super.setType(type)
        super.setTitle(title)
        super.setDetail(detail)
        super.setProperty("timestamp", timestamp)
    }


    companion object {

        fun badRequest(cause: DomainException): ErrorResponse = ErrorResponse(cause, HttpStatus.BAD_REQUEST)

        fun conflict(cause: DomainException): ErrorResponse = ErrorResponse(cause, HttpStatus.CONFLICT)

        fun internalServerError(cause: Throwable): ErrorResponse =
            ErrorResponse(
                null,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                URI.create("unexpected-error"),
                HttpStatus.INTERNAL_SERVER_ERROR.name,
                cause.message ?: cause.toString()
            )
    }

}

fun ErrorResponse.toResponseEntity(): ResponseEntity<ErrorResponse> = ResponseEntity.of(this).build()
