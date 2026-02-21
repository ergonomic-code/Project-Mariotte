package pro.azhidkov.mariotte.core.reservations

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import pro.azhidkov.mariotte.core.rooms.RoomType
import pro.azhidkov.mariotte.core.hotels.HotelRef
import java.time.LocalDate
import java.util.*

/**
 * Публичное представление сущности бронирования номера в отеле в системе.
 * * Слой в Функциональной архитектуре: чистое ядро
 * * Тип блока в структурном дизайне: н/а
 * * Слой в чистой архитектуре: юз-кейсов/адаптеров
 * * Тип блока в Эргономичной архитектуре: н/а
 */
@Table("reservations")
data class ReservationDetailsView(
    @Id val id: UUID,
    val hotelRef: HotelRef,
    val roomType: RoomType,
    val email: String,
    val from: LocalDate,
    val period: ReservationPeriod
)
