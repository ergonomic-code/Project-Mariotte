package pro.azhidkov.mariotte.core.reservations

import org.springframework.data.jdbc.core.JdbcAggregateOperations
import org.springframework.data.jdbc.core.findById
import org.springframework.data.jdbc.repository.support.SimpleJdbcRepository
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import pro.azhidkov.mariotte.core.rooms.RoomType
import pro.azhidkov.mariotte.core.hotels.HotelRef
import pro.azhidkov.platform.spring.data.getRequiredPersistentEntity
import java.time.LocalDate
import java.util.*

/**
 * Репозиторий сущностей "Бронирование".
 * * Слой в Функциональной архитектуре: императивная оболочка
 * * Тип блока в структурном дизайне: эфферентные и афферентные
 * * Слой в чистой архитектуре: инфраструктура
 * * Тип блока в Эргономичной архитектуре: контейнер ресурса
 *
 * Метки в коде:
 * 1. Запрос с помощью generate_series формирует псевдо таблицу со строками для каждого дня между from и to включительно,
 *    затем присоединяет к ним брони, которые пресекаются с этими днями (одна бронь может быть
 *    присоеденена к несклькоим дня), группирует результат по дате и считает кол-во броней.
 */
@Repository
class ReservationsRepo(
    private val jdbcClient: JdbcClient,
    private val jdbcAggregateOperations: JdbcAggregateOperations
) : SimpleJdbcRepository<Reservation, UUID>(
    jdbcAggregateOperations,
    jdbcAggregateOperations.getRequiredPersistentEntity<Reservation>(),
    jdbcAggregateOperations.converter
) {

    /**
     * Возвращает количество пересекающихся резерваций по дням в периоде [from..to ].
     */
    fun getReservationsAmountPerDate(
        hotel: HotelRef,
        roomType: RoomType,
        from: LocalDate,
        to: LocalDate
    ): Map<LocalDate, Int> {
        return jdbcClient.sql( // 1
            """
                SELECT d::date, count(*)
                FROM generate_series(:from::date, :to::date, '1 days') AS d(date)
                JOIN reservations r ON d.date BETWEEN r.from AND r.from + r.period
                WHERE
                  hotel_ref = :hotel AND
                  room_type = :roomType
                GROUP BY d
                ORDER BY d
            """
        )
            .param("hotel", hotel.id)
            .param("roomType", roomType.name)
            .param("from", from)
            .param("to", to)
            .query { rs, _ -> rs.getObject(1, LocalDate::class.java) to rs.getInt(2) }
            .list()
            .toMap()
    }

    fun findDetailsById(reservationId: UUID): ReservationDetailsView? {
        return jdbcAggregateOperations.findById<ReservationDetailsView>(reservationId)
    }

}
