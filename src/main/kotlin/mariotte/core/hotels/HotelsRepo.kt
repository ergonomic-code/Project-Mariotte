package pro.azhidkov.mariotte.core.hotels

import org.springframework.data.jdbc.core.JdbcAggregateOperations
import org.springframework.data.jdbc.repository.support.SimpleJdbcRepository
import org.springframework.stereotype.Repository
import pro.azhidkov.platform.spring.data.getRequiredPersistentEntity
import java.util.*


/**
 * Репозиторий сущностей "Отель".
 * * Слой в Функциональной архитектуре: императивная оболочка
 * * Тип блока в структурном дизайне: эфферентные и афферентные
 * * Слой в чистой архитектуре: инфраструктура
 * * Тип блока в Эргономичной архитектуре: контейнер ресурса
 */
@Repository
class HotelsRepo(
    jdbcAggregateOperations: JdbcAggregateOperations
) : SimpleJdbcRepository<Hotel, UUID>(
    jdbcAggregateOperations,
    jdbcAggregateOperations.getRequiredPersistentEntity<Hotel>(),
    jdbcAggregateOperations.converter
)
