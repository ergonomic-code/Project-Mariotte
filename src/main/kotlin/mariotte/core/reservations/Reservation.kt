package pro.azhidkov.mariotte.core.reservations

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import pro.azhidkov.mariotte.core.rooms.RoomType
import pro.azhidkov.mariotte.core.hotels.HotelRef
import pro.azhidkov.platform.java.util.Uuids
import java.time.Instant
import java.time.LocalDate
import java.util.*


/**
 * Сущность представляющая бронирование номера в отеле в системе.
 * * Слой в Функциональной архитектуре: чистое ядро
 * * Тип блока в структурном дизайне: н/а
 * * Слой в чистой архитектуре: сущности
 * * Тип блока в Эргономичной архитектуре: запись ресурса
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
