package pro.azhidkov.mariotte.fixtures.object_mothers

import pro.azhidkov.mariotte.core.rooms.Room
import pro.azhidkov.mariotte.core.rooms.RoomType
import pro.azhidkov.mariotte.core.hotels.HotelRef


/**
 * Фабрика тестовых объектов номеров.
 */
object RoomsObjectMother {

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
        faker.number().numberBetween(1, 10_000)

}
