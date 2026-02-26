package pro.azhidkov.mariotte.domain.reservations

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import pro.azhidkov.mariotte.domain.hotels.HotelRef
import pro.azhidkov.mariotte.domain.rooms.RoomType
import pro.azhidkov.platform.java.util.Uuids
import java.time.Instant
import java.time.LocalDate
import java.util.*


/**
 * Сущность представляющая бронирование номера в отеле в системе.
 */
@Table("reservations")
data class Reservation(
    @Column("hotel_ref")
    val hotel: HotelRef,
    val roomType: RoomType,
    val email: String,
    val from: LocalDate,
    val period: ReservationPeriod,

    @Id
    val id: UUID = Uuids.v7(),
    @CreatedDate
    val createdAt: Instant = Instant.now(),
    @LastModifiedDate
    val modifiedAt: Instant? = null,
    @Version
    val version: Int = 0
)

val Reservation.to: LocalDate
    get() = from.plusDays(period.days.toLong())
