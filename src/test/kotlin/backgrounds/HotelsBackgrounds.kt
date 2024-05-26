package pro.azhidkov.mariotte.backgrounds

import org.springframework.stereotype.Component
import pro.azhidkov.mariotte.core.hotels.HotelsService
import pro.azhidkov.mariotte.core.hotels.rooms.Room
import pro.azhidkov.mariotte.core.hotels.root.Hotel
import pro.azhidkov.mariotte.core.hotels.root.HotelRef
import pro.azhidkov.mariotte.fixtures.HotelsObjectMother
import pro.azhidkov.mariotte.fixtures.RoomsObjectMother


@Component
class HotelsBackgrounds(
    private val hotelsService: HotelsService
) {

    fun createHotel(
        hotel: () -> Hotel = { HotelsObjectMother.hotel() },
        rooms: (HotelRef) -> List<Room> = { RoomsObjectMother.rooms(hotel = it, 1) }
    ): Hotel {
        val persistedHotel = hotelsService.addHotel(hotel())
        hotelsService.addRooms(rooms(persistedHotel.ref()))
        return persistedHotel
    }

}