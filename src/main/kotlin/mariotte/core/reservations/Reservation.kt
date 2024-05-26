package pro.azhidkov.mariotte.core.reservations

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import pro.azhidkov.mariotte.core.hotels.rooms.RoomType
import pro.azhidkov.mariotte.core.hotels.root.HotelRef
import java.time.LocalDate


@Table("reservations")
data class Reservation(
    @JsonProperty("hotel")
    val hotelRef: HotelRef,

    val roomType: RoomType,

    val email: String,

    val from: LocalDate,

    val to: LocalDate,

    @Id
    val id: Int = 0
)