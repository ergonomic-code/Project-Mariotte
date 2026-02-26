package pro.azhidkov.platform.domain.errors

import pro.azhidkov.platform.java.lang.inKebabCase
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.jvmErasure

/**
 * Стандартный класс ошибки для случая, когда по переданному ключу не удаётся найти сущность в репозитории.
 */
class EntityNotFoundException(
    val type: String,
    val keys: List<Pair<String, Any?>>,
    override val message: String = "Entity of type $type not found by ${keys.format()}",
    errorCode: String = "resource-not-found",
    cause: Throwable? = null
) : DomainException(
    message,
    cause,
    errorCode = errorCode
) {

    init {
        require(keys.isNotEmpty())
    }

    constructor(prop: KProperty1<*, Any?>, key: Any) : this(
        prop.parameters[0].type.jvmErasure.simpleName!!,
        listOf(prop.name to key),
        errorCode = "${prop.parameters[0].type.jvmErasure.simpleName!!.inKebabCase}-not-found"
    )

    constructor(entityType: KClass<*>, key: Any) : this(
        entityType.simpleName ?: "Entity",
        listOf("key" to key),
        errorCode = "${entityType.simpleName?.inKebabCase ?: "entity"}-not-found"
    )

}

private fun List<Pair<String, Any?>>.format(): String =
    when (size) {
        1 -> first().let(::formatAttrValue)
        else -> this.take(4).joinToString(", ", prefix = "[", postfix = "]", transform = ::formatAttrValue)
    }

private fun formatAttrValue(attrValue: Pair<String, Any?>): String =
    "${attrValue.first}=${attrValue.second}"
