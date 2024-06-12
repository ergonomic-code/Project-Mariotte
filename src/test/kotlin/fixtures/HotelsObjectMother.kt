package pro.azhidkov.mariotte.fixtures

import pro.azhidkov.mariotte.core.hotels.rooms.RoomType
import pro.azhidkov.mariotte.core.hotels.root.Hotel
import pro.azhidkov.mariotte.core.hotels.root.HotelRef

/**
 * Фабрика тестовых объектов отелей
 */
object HotelsObjectMother {

    // Вставляется в data.sql
    val theHotel = HotelInfo(
        Hotel.ref(1),
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