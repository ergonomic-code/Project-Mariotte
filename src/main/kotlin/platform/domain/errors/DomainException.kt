package pro.azhidkov.platform.domain.errors

import pro.azhidkov.platform.java.lang.inKebabCase


/**
 * Корневое исключение ожидаемых ошибок в системе.
 *
 * `errorCode` используется как машинно-читаемый идентификатор причины ошибки (например, для
 * `ProblemDetail.type`/`ErrorResponse.type` в HTTP API).
 */
open class DomainException(
    msg: String? = null,
    cause: Throwable? = null,
    errorCode: String? = null
) : Exception(msg, cause) {

    val errorCode = errorCode
        ?: this.javaClass.simpleName.inKebabCase.removeSuffix("-exception")

}
