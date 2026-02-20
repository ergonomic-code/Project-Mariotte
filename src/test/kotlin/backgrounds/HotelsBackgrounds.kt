package pro.azhidkov.mariotte.backgrounds

import org.springframework.stereotype.Component
import pro.azhidkov.mariotte.core.rooms.Room
import pro.azhidkov.mariotte.core.rooms.RoomsRepo
import pro.azhidkov.mariotte.core.hotels.Hotel
import pro.azhidkov.mariotte.core.hotels.HotelRef
import pro.azhidkov.mariotte.core.hotels.HotelsRepo
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
    private val hotelsRepo: HotelsRepo,
    private val roomsRepo: RoomsRepo
) {

    fun createHotel(
        hotel: () -> Hotel = { HotelsObjectMother.hotel() },
        rooms: (HotelRef) -> List<Room> = { RoomsObjectMother.rooms(hotel = it, 1) }
    ): Hotel {
        val persistedHotel = hotelsRepo.save(hotel())
        roomsRepo.saveAll(rooms(persistedHotel.ref()))
        return persistedHotel
    }

}
