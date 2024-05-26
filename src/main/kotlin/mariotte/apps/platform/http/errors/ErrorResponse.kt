package pro.azhidkov.mariotte.apps.platform.http.errors

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import pro.azhidkov.platform.domain.errors.DomainException
import java.net.URI
import java.time.Instant

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

    fun toResponseEntity(): ResponseEntity<ErrorResponse> = ResponseEntity.of(this).build()

    companion object {

        fun badRequest(cause: DomainException): ErrorResponse = ErrorResponse(cause, HttpStatus.BAD_REQUEST)

        fun conflict(cause: DomainException): ErrorResponse = ErrorResponse(cause, HttpStatus.CONFLICT)
    }

}