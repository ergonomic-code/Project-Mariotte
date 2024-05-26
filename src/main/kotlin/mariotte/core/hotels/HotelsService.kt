package pro.azhidkov.mariotte.core.hotels

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import pro.azhidkov.mariotte.core.hotels.rooms.Room
import pro.azhidkov.mariotte.core.hotels.rooms.RoomType
import pro.azhidkov.mariotte.core.hotels.rooms.RoomsRepo
import pro.azhidkov.mariotte.core.hotels.root.Hotel
import pro.azhidkov.mariotte.core.hotels.root.HotelRef
import pro.azhidkov.mariotte.core.hotels.root.HotelsRepo


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

    fun getCapacityForUpdate(hotel: HotelRef, roomType: RoomType): Int {
        return roomsRepo.findAllRoomsByHotelRefAndRoomType(hotel, roomType)
            .count()
    }

    fun findById(hotelId: Int): Hotel? {
        return hotelsRepo.findByIdOrNull(hotelId)
    }

}