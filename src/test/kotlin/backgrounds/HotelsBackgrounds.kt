package pro.azhidkov.mariotte.backgrounds

import org.springframework.stereotype.Component
import pro.azhidkov.mariotte.core.hotels.HotelsService
import pro.azhidkov.mariotte.core.hotels.rooms.Room
import pro.azhidkov.mariotte.core.hotels.root.Hotel
import pro.azhidkov.mariotte.core.hotels.root.HotelRef
import pro.azhidkov.mariotte.fixtures.HotelsObjectMother
import pro.azhidkov.mariotte.fixtures.RoomsObjectMother

/**
 * Бэкграунд для работы с отелями.
 *
 * По [стандартной структуре кода тестов по ЭП](https://azhidkov.pro/microposts/24/03/trainer-advisor-testing-theory/#_виды_тестового_кода_и_их_общая_структура)
 * бэкграунды предназначены для сетапа сложной фикстуры.
 * В данном случае бэкгрунд предоставляет метод для одновременного создания отеля и его номеров.
 */
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