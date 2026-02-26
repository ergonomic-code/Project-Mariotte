package pro.azhidkov.mariotte.domain.hotels

import org.springframework.data.jdbc.core.JdbcAggregateOperations
import org.springframework.data.jdbc.repository.support.SimpleJdbcRepository
import org.springframework.stereotype.Repository
import pro.azhidkov.platform.spring.data.getRequiredPersistentEntity
import java.util.*


/**
 * Репозиторий сущностей "Отель".
 */
@Repository
class HotelsRepo(
    jdbcAggregateOperations: JdbcAggregateOperations
) : SimpleJdbcRepository<Hotel, UUID>(
    jdbcAggregateOperations,
    jdbcAggregateOperations.getRequiredPersistentEntity<Hotel>(),
    jdbcAggregateOperations.converter
)
