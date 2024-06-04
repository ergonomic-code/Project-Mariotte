package pro.azhidkov.mariotte.core.reservations

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.Positive
import org.hibernate.validator.constraints.time.DurationMin
import pro.azhidkov.mariotte.core.hotels.rooms.RoomType
import java.time.LocalDate
import java.time.Period


data class RoomReservationRequest(
    val hotelId: Int,
    val roomType: RoomType,
    val email: String,
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val from: LocalDate,
    val period: ReservationPeriod
)