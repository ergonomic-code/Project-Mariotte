package pro.azhidkov.mariotte.fixtures

import pro.azhidkov.mariotte.core.rooms.Room
import pro.azhidkov.mariotte.core.rooms.RoomType
import pro.azhidkov.mariotte.core.hotels.HotelRef


/**
 * Фабрика тестовых объектов номеров.
 */
object RoomsObjectMother {

    fun rooms(
        hotel: HotelRef = HotelsObjectMother.theHotel.ref,
        count: Int
    ) = (1..count).map { room(hotel = hotel) }

    fun room(
        hotel: HotelRef = HotelsObjectMother.theHotel.ref,
        roomType: RoomType = RoomType.entries.randomElement(),
        roomNumber: Int = randomRoomNumber()
    ) = Room(
        hotel = hotel,
        roomType = roomType,
        roomNumber = roomNumber
    )

    fun randomRoomNumber() =
        random.nextInt(1, 10_000)

}