package pro.azhidkov.platform.domain.errors

import pro.azhidkov.mariotte.apps.platform.java.lang.inKebabCase


open class DomainException(msg: String? = null, cause: Throwable? = null, errorCode: String? = null) : Exception(msg, cause) {

    val errorCode = errorCode
        ?: this.javaClass.simpleName.inKebabCase.removeSuffix("-exception")

}