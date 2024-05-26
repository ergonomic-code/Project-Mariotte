package pro.azhidkov.platform.domain.errors

import pro.azhidkov.mariotte.apps.platform.java.lang.inKebabCase
import kotlin.reflect.KClass

class EntityNotFoundException(
    entityType: KClass<*>,
    key: Any
) : DomainException(
    "Entity of type ${entityType.simpleName} not found by key $key",
    errorCode = "${entityType.simpleName?.inKebabCase ?: "entity"}-not-found"
)