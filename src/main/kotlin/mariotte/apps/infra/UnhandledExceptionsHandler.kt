package pro.azhidkov.mariotte.apps.infra

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import pro.azhidkov.mariotte.apps.platform.spring.http.ErrorResponse
import java.net.URI


/**
 * У Spring-а есть стандартная ручка для включения рендеринга тел ошибок в формате [Problem details](https://datatracker.ietf.org/doc/html/rfc7807),
 * однако по стандарту Problem Details почему-то не включает в себя время возникновения ошибки.
 * Этот класс исправляет эту недоработку.
 */
@RestControllerAdvice
class UnhandledExceptionsHandler : ResponseEntityExceptionHandler() {

    override fun handleExceptionInternal(
        ex: Exception,
        body: Any?,
        headers: HttpHeaders,
        statusCode: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val errorResponseBody = when (body) {
            is ErrorResponse ->
                body

            is ProblemDetail ->
                ErrorResponse(body, ex)

            else ->
                ErrorResponse(
                    URI.create(request.contextPath),
                    statusCode.value(),
                    URI.create("unexpected-error"),
                    "Unexpected error",
                    body?.toString() ?: ex.message ?: ex.toString()
                )
        }
        return super.handleExceptionInternal(ex, errorResponseBody, headers, statusCode, request)
    }

}