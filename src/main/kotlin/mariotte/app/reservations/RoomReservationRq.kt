package pro.azhidkov.mariotte.app.reservations

import com.fasterxml.jackson.annotation.JsonFormat
import pro.azhidkov.mariotte.domain.reservations.ReservationPeriod
import pro.azhidkov.mariotte.domain.rooms.RoomType
import java.time.LocalDate
import java.util.*


/**
 * DTO запроса на бронирование номера в отеле.
 * Используется как входная модель HTTP-порта.
 */
data class RoomReservationRq(
    val hotelId: UUID,
    val roomType: RoomType,
    val email: String,
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val from: LocalDate,
    val period: ReservationPeriod
)
