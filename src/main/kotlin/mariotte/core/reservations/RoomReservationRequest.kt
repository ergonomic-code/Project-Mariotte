package pro.azhidkov.mariotte.core.reservations

import com.fasterxml.jackson.annotation.JsonFormat
import pro.azhidkov.mariotte.core.hotels.rooms.RoomType
import java.time.LocalDate


data class RoomReservationRequest(
    val hotelId: Int,
    val roomType: RoomType,
    val email: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val from: LocalDate,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val to: LocalDate
)