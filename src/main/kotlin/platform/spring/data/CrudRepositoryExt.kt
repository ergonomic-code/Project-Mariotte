package pro.azhidkov.platform.spring.data

import org.springframework.data.jdbc.core.JdbcAggregateOperations
import org.springframework.data.mapping.PersistentEntity

/**
 * Этот метод дурно попахивает завязкой на детали реализации Spring Data Jdbc, но:
 * 1. он использует только публичное и задокументированное API, в документации к которому нет никаких предупреждений
 *    или запретов на его использование;
 * 2. даже если эти детали всё-таки изменятся, знание о них скрыто в этом методе и это будет единственным местом,
 *    которое придётся менять в этом случае.
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> JdbcAggregateOperations.getRequiredPersistentEntity(): PersistentEntity<T, *> =
    this.converter.mappingContext.getRequiredPersistentEntity(T::class.java) as PersistentEntity<T, *>
