package pro.azhidkov.mariotte.fixtures.object_mothers

import pro.azhidkov.mariotte.domain.rooms.Room
import pro.azhidkov.mariotte.domain.rooms.RoomType
import pro.azhidkov.mariotte.domain.hotels.HotelRef


/**
 * Фабрика тестовых объектов номеров.
 */
object RoomsObjectMother {

    fun aRoom(
        hotel: HotelRef = HotelsObjectMother.theHotel.ref,
        roomType: RoomType = RoomType.entries.randomElement(),
        roomNumber: Int = aRoomNumber()
    ) = Room(
        hotel = hotel,
        roomType = roomType,
        roomNumber = roomNumber
    )

    fun aRoomNumber() =
        faker.number().numberBetween(1, 10_000)

}
