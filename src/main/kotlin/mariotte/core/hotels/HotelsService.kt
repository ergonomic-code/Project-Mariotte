package pro.azhidkov.mariotte.core.hotels

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import pro.azhidkov.mariotte.core.hotels.rooms.Room
import pro.azhidkov.mariotte.core.hotels.rooms.RoomType
import pro.azhidkov.mariotte.core.hotels.rooms.RoomsRepo
import pro.azhidkov.mariotte.core.hotels.root.Hotel
import pro.azhidkov.mariotte.core.hotels.root.HotelRef
import pro.azhidkov.mariotte.core.hotels.root.HotelsRepo


/**
 * Контейнер сложного ресурса логического агрегата "Отели".
 * * Слой в Функциональной архитектуре: императивная оболочка
 * * Тип блока в структурном дизайне: эфферентные и афферентные
 * * Слой в чистой архитектуре: н/а?, инфраструктура
 * * Тип блока в Эргономичной архитектуре: контейнер сложного ресурса
 */
@Service
class HotelsService(
    private val hotelsRepo: HotelsRepo,
    private val roomsRepo: RoomsRepo
) {

    fun addHotel(hotel: Hotel): Hotel =
        hotelsRepo.save(hotel)

    fun addRooms(rooms: Iterable<Room>) {
        roomsRepo.saveAll(rooms)
    }

    /**
     * Захватывает блокировку на номера определённого типа в отеле и возвращает
     * общее количество имеющихся в отеле номеров этого типа.
     * В случае отсутствия таких номеров возражает null.
     */
    fun getCapacityForUpdate(hotel: HotelRef, roomType: RoomType): Int? {
        roomsRepo.findTop1AndLockByHotelAndRoomType(hotel, roomType)
        return roomsRepo.countRoomsByHotelAndRoomType(hotel, roomType)
            .takeIf { it > 0 }
    }

    fun findById(hotelId: Int): Hotel? {
        return hotelsRepo.findByIdOrNull(hotelId)
    }

}