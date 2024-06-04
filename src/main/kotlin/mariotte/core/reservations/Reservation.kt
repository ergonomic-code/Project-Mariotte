package pro.azhidkov.mariotte.core.reservations

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import pro.azhidkov.mariotte.core.hotels.rooms.RoomType
import pro.azhidkov.mariotte.core.hotels.root.HotelRef
import java.time.LocalDate


/**
 * Сущность представляющая бронирование номера в отеле в системе.
 * * Слой в Функциональной архитектуре: чистое ядро
 * * Тип блока в структурном дизайне: н/а
 * * Слой в чистой архитектуре: сущности
 * * Тип блока в Эргономичной архитектуре: запись ресурса
 *
 * Метки в коде:
 * 1. Неоднозначное решение - в идеально мире ни слой ядра, ни слой хранения данных не должен ничего знать о
 *    представлении сущности в виде JSON.
 *    Однако в данном конкретном случае создание отдельной DTO ради одной аннотации влечёт излишнее дублирование и
 *    не даёт ничего взамен, помимо идеологической чистоты.
 */
@Table("reservations")
data class Reservation(
    @field:JsonProperty("hotel")
    val hotelRef: HotelRef,
    val roomType: RoomType,
    val email: String,
    val from: LocalDate,
    val period: ReservationPeriod,

    @Id
    val id: Int = 0
)

val Reservation.to: LocalDate
    get() = from.plusDays(period.days.toLong())