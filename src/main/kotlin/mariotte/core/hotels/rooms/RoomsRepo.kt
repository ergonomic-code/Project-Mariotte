package pro.azhidkov.mariotte.core.hotels.rooms

import org.springframework.data.relational.core.sql.LockMode
import org.springframework.data.relational.repository.Lock
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import pro.azhidkov.mariotte.core.hotels.root.HotelRef


/**
 * Репозиторий сущностей "Номер".
 * * Слой в Функциональной архитектуре: императивная оболочка
 * * Тип блока в структурном дизайне: эфферентные и афферентные
 * * Слой в чистой архитектуре: инфраструктура
 * * Тип блока в Эргономичной архитектуре: контейнер ресурса
 */
@Repository
interface RoomsRepo : CrudRepository<Room, Int> {

    /**
     * Заблокировать любой номер определённого типа, см. [PostgreSQL Explicit Locking](https://www.postgresql.org/docs/current/explicit-locking.html)
     */
    @Lock(LockMode.PESSIMISTIC_WRITE)
    fun findTop1AndLockByHotelRefAndRoomType(hotelRef: HotelRef, roomType: RoomType): Room?

    /**
     * Найти все номера определенного типа в отеле и заблокировать соответствующие строки БД.
     */
    fun countRoomsByHotelRefAndRoomType(hotelRef: HotelRef, roomType: RoomType): Int

}