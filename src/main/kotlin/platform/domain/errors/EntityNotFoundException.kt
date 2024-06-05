package pro.azhidkov.platform.domain.errors

import pro.azhidkov.platform.lang.inKebabCase
import kotlin.reflect.KClass

/**
 * Стандартный класс для сигнализации о передаче ключа сущности, по которому не удаётся найти сущность в репозитории
 */
class EntityNotFoundException(
    entityType: KClass<*>,
    key: Any
) : DomainException(
    "Entity of type ${entityType.simpleName} not found by key $key",
    errorCode = "${entityType.simpleName?.inKebabCase ?: "entity"}-not-found"
)