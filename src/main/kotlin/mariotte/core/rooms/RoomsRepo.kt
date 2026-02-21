package pro.azhidkov.mariotte.core.rooms

import org.springframework.data.jdbc.core.JdbcAggregateOperations
import org.springframework.data.jdbc.repository.support.SimpleJdbcRepository
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import pro.azhidkov.mariotte.core.hotels.HotelRef
import pro.azhidkov.platform.spring.data.getRequiredPersistentEntity
import java.util.*


/**
 * Репозиторий сущностей "Номер".
 * * Слой в Функциональной архитектуре: императивная оболочка
 * * Тип блока в структурном дизайне: эфферентные и афферентные
 * * Слой в чистой архитектуре: инфраструктура
 * * Тип блока в Эргономичной архитектуре: контейнер ресурса
 */
@Repository
class RoomsRepo(
    private val jdbcClient: JdbcClient,
    jdbcAggregateOperations: JdbcAggregateOperations
) : SimpleJdbcRepository<Room, UUID>(
    jdbcAggregateOperations,
    jdbcAggregateOperations.getRequiredPersistentEntity<Room>(),
    jdbcAggregateOperations.converter
) {

    /**
     * Захватывает эксклюзивную блокировку на номера определённого типа в отеле и возвращает
     * общее количество имеющихся в отеле номеров этого типа.
     * В случае отсутствия таких номеров возвращает null.
     *
     * см. [PostgreSQL Explicit Locking](https://www.postgresql.org/docs/current/explicit-locking.html)
     */
    fun getCapacityForUpdate(hotelRef: HotelRef, roomType: RoomType): Int? {
        return jdbcClient.sql(
            """
                WITH locked_rooms AS (
                    SELECT id
                    FROM rooms
                    WHERE hotel_ref = :hotelRef
                      AND room_type = :roomType
                    FOR UPDATE
                )
                SELECT COUNT(*)
                FROM locked_rooms
            """
        )
            .param("hotelRef", hotelRef.id)
            .param("roomType", roomType.name)
            .query(Int::class.java)
            .single()
            .takeIf { it > 0 }
    }

}
