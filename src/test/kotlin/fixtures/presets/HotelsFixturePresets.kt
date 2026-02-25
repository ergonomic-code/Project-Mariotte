package pro.azhidkov.mariotte.fixtures.presets

import org.springframework.stereotype.Component
import pro.azhidkov.mariotte.core.rooms.Room
import pro.azhidkov.mariotte.core.rooms.RoomType
import pro.azhidkov.mariotte.core.rooms.RoomsRepo
import pro.azhidkov.mariotte.core.hotels.Hotel
import pro.azhidkov.mariotte.core.hotels.HotelsRepo
import pro.azhidkov.mariotte.fixtures.object_mothers.HotelsObjectMother
import pro.azhidkov.mariotte.fixtures.object_mothers.RoomsObjectMother

/**
 * Fixture presets для подготовки данных по отелям в сценарных тестах.
 * Содержит многоресурсные сценарии вставки фикстур.
 */
@Component
class HotelsFixturePresets(
    private val hotelsRepo: HotelsRepo,
    private val roomsRepo: RoomsRepo
) {

    fun insertFixture(fixture: NewHotelFixture): NewHotelFixture {
        val savedHotel = hotelsRepo.save(fixture.hotel)
        val savedRooms = roomsRepo.saveAll(fixture.rooms)
        return NewHotelFixture(savedHotel, savedRooms)
    }

}

data class NewHotelFixture(
    val hotel: Hotel = HotelsObjectMother.aHotel(),
    val rooms: List<Room> = emptyList()
) {

    companion object {

        fun ofHotelWithSingleRoom(roomType: RoomType): NewHotelFixture {
            val hotel = HotelsObjectMother.aHotel()
            return NewHotelFixture(
                hotel = hotel,
                rooms = listOf(RoomsObjectMother.aRoom(hotel.ref(), roomType))
            )
        }

    }

}
