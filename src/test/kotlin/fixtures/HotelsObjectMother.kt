package pro.azhidkov.mariotte.fixtures

import pro.azhidkov.mariotte.core.hotels.rooms.RoomType
import pro.azhidkov.mariotte.core.hotels.root.Hotel
import pro.azhidkov.mariotte.core.hotels.root.HotelRef
import java.util.*

/**
 * Фабрика тестовых объектов отелей
 */
object HotelsObjectMother {

    // Вставляется в data.sql
    val theHotel = HotelInfo(
        Hotel.ref(UUID.fromString("0196f8b0-2b3c-7f12-8a45-1c2d3e4f5a6b")),
        mapOf(RoomType.LUX to 1)
    )

    fun hotel(): Hotel =
        Hotel()

}

data class HotelInfo(
    val ref: HotelRef,
    val capacity: Map<RoomType, Int>
) {

    fun availableRoomTypes(): Iterable<RoomType> =
        capacity.keys

}
