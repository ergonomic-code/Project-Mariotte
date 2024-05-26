package pro.azhidkov.mariotte.core.hotels.rooms

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import pro.azhidkov.mariotte.core.hotels.root.HotelRef


@Table("rooms")
data class Room(
    val hotelRef: HotelRef,
    val roomType: RoomType,
    val roomNumber: Int,

    @Id
    val id: Int = 0
)
