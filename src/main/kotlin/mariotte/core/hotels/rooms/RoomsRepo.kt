package pro.azhidkov.mariotte.core.hotels.rooms

import org.springframework.data.relational.core.sql.LockMode
import org.springframework.data.relational.repository.Lock
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import pro.azhidkov.mariotte.core.hotels.root.HotelRef


@Repository
interface RoomsRepo : CrudRepository<Room, Int> {

    @Lock(LockMode.PESSIMISTIC_WRITE)
    fun findAllRoomsByHotelRefAndRoomType(hotelRef: HotelRef, roomType: RoomType): Iterable<Room>

}