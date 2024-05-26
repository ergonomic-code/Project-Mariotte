package pro.azhidkov.mariotte.apps.platform.spring.http

import org.springframework.http.ResponseEntity
import pro.azhidkov.mariotte.apps.platform.http.errors.ErrorResponse
import pro.azhidkov.platform.domain.errors.DomainException


fun badRequestOf(cause: DomainException): ResponseEntity<ErrorResponse> =
    ErrorResponse.badRequest(cause).toResponseEntity()

fun conflictOf(cause: DomainException): ResponseEntity<ErrorResponse> =
    ErrorResponse.conflict(cause).toResponseEntity()

