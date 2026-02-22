package pro.azhidkov.mariotte.apps.platform.spring.http

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import pro.azhidkov.platform.domain.errors.DomainException

/**
 * Утилиты над типом ResponseEntity
 */

/**
 * Конструктор ResponseEntity для статуса 201 без заголовка Location
 */
fun <T : Any> created(body: T): ResponseEntity<T> =
    ResponseEntity.status(HttpStatus.CREATED)
        .body(body)

/**
 * Конструктор ResponseEntity для статуса 404
 */
fun notFoundOf(cause: DomainException): ResponseEntity<ErrorResponse> =
    ErrorResponse.notFound(cause).toResponseEntity()

/**
 * Конструктор ResponseEntity для статуса 409
 */
fun conflictOf(cause: DomainException): ResponseEntity<ErrorResponse> =
    ErrorResponse.conflict(cause).toResponseEntity()

/**
 * Конструктор ResponseEntity для статуса 422
 */
fun unprocessableEntityOf(cause: DomainException): ResponseEntity<ErrorResponse> =
    ErrorResponse.unprocessableEntity(cause).toResponseEntity()
