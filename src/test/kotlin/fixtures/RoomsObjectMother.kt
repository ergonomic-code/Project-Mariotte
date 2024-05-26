package pro.azhidkov.mariotte.fixtures

import pro.azhidkov.mariotte.core.hotels.rooms.Room
import pro.azhidkov.mariotte.core.hotels.rooms.RoomType
import pro.azhidkov.mariotte.core.hotels.root.HotelRef


object RoomsObjectMother {

    fun rooms(
        hotel: HotelRef = HotelsObjectMother.theHotel(),
        count: Int
    ) = (1..count).map { room(hotel = hotel) }

    fun room(
        hotel: HotelRef = HotelsObjectMother.theHotel(),
        roomType: RoomType = RoomType.entries.randomElement(),
        roomNumber: Int = randomRoomNumber()
    ) = Room(
        hotelRef = hotel,
        roomType = roomType,
        roomNumber = roomNumber
    )

    fun randomRoomNumber() =
        random.nextInt(1, 10_000)

}