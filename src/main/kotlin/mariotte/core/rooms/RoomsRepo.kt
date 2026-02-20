package pro.azhidkov.mariotte.core.rooms

import org.springframework.data.relational.core.sql.LockMode
import org.springframework.data.relational.repository.Lock
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import pro.azhidkov.mariotte.core.hotels.HotelRef
import java.util.*


/**
 * Репозиторий сущностей "Номер".
 * * Слой в Функциональной архитектуре: императивная оболочка
 * * Тип блока в структурном дизайне: эфферентные и афферентные
 * * Слой в чистой архитектуре: инфраструктура
 * * Тип блока в Эргономичной архитектуре: контейнер ресурса
 */
@Repository
interface RoomsRepo : CrudRepository<Room, UUID> {

    /**
     * Заблокировать любой номер определённого типа, см. [PostgreSQL Explicit Locking](https://www.postgresql.org/docs/current/explicit-locking.html)
     */
    @Lock(LockMode.PESSIMISTIC_WRITE)
    fun findTop1AndLockByHotelAndRoomType(hotelRef: HotelRef, roomType: RoomType): Room?

    /**
     * Найти все номера определенного типа в отеле и заблокировать соответствующие строки БД.
     */
    fun countRoomsByHotelAndRoomType(hotelRef: HotelRef, roomType: RoomType): Int

    /**
     * Захватывает блокировку на номера определённого типа в отеле и возвращает
     * общее количество имеющихся в отеле номеров этого типа.
     * В случае отсутствия таких номеров возвращает null.
     */
    fun getCapacityForUpdate(hotelRef: HotelRef, roomType: RoomType): Int? {
        findTop1AndLockByHotelAndRoomType(hotelRef, roomType)
        return countRoomsByHotelAndRoomType(hotelRef, roomType)
            .takeIf { it > 0 }
    }

}
