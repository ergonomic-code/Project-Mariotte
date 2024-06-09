package pro.azhidkov.mariotte.apps.platform.spring.http

import org.springframework.http.ResponseEntity
import pro.azhidkov.platform.domain.errors.DomainException

/**
 * Утилиты над типом ResponseEntity
 */


/**
 * Конструктор ResponseEntity для статуса 409
 */
fun conflictOf(cause: DomainException): ResponseEntity<ErrorResponse> =
    ErrorResponse.conflict(cause).toResponseEntity()