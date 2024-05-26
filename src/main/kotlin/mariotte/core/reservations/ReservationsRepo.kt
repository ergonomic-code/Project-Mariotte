package pro.azhidkov.mariotte.core.reservations

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import pro.azhidkov.mariotte.core.hotels.rooms.RoomType
import pro.azhidkov.mariotte.core.hotels.root.HotelRef
import pro.azhidkov.platform.spring.jdbc.PairRowMapper
import java.time.LocalDate


/**
 * Репозиторий сущностей "Бронирование"
 * * Слой в Функциональной архитектуре: императивная оболочка
 * * Тип блока в структурном дизайне: эфферентные и афферентные
 * * Слой в чистой архитектуре: инфраструктура
 * * Тип блока в Эргономичной архитектуре: контейнер ресурса
 *
 * Метки в коде:
 * 1. Запрос с помощью generate_series формирует псевдо таблицу со строками для каждого дня между from и to включительно,
 *    затем присоединяет к ним брони, которые пресекаются с этими днями (одна бронь может быть
 *    присоеденена к несклькоим дня), группирует результат по дате и считает кол-во броней.
 * 2. Дурнопхнущее решение, обусловленное ограничениями, накладываемыми моделью программирования Spring Data.
 *    Публичным явлется вариант, который возвращает Map, а не Iterable.
 */
@Repository
interface ReservationsRepo : CrudRepository<Reservation, Int> {

    /**
     * Запрос формирует псевдо таблицу со строками для каждого дня между from и to включительно,
     * затем присоединяет к ним брони, которые пресекаются с этими днями (одна бронь может быть
     * присоеденена к несклькоим дня), группирует результат по дате и считает кол-во броней.
     */
    @Query( // 1
        """
            SELECT d::date, count(*)
            FROM generate_series(:from::date, :to::date, '1 days') AS d(date) 
            JOIN reservations r ON d.date BETWEEN r.from AND r.from + r.period
            WHERE
              hotel_ref = :hotel AND
              room_type = :roomType
            GROUP BY d
            ORDER BY d
    """,
        rowMapperClass = PairRowMapper::class
    )
    fun getReservationsAmountPerDate( // 2
        hotel: Int,
        roomType: RoomType,
        from: LocalDate,
        to: LocalDate
    ): Iterable<Pair<LocalDate, Int>>

    fun findDetailsById(reservationId: Int): ReservationDetails?

}

fun ReservationsRepo.getReservationsAmountPerDate(
    hotel: HotelRef,
    roomType: RoomType,
    from: LocalDate,
    to: LocalDate
) = this.getReservationsAmountPerDate(hotel.id!!, roomType, from, to)
    .toMap()

