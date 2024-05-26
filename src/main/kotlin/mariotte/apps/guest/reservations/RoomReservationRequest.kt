package pro.azhidkov.mariotte.apps.guest.reservations

import com.fasterxml.jackson.annotation.JsonFormat
import pro.azhidkov.mariotte.core.hotels.rooms.RoomType
import pro.azhidkov.mariotte.core.reservations.ReservationPeriod
import java.time.LocalDate


/**
 * DTO запроса на бронирование номера в отеле.
 * * Слой в Функциональной архитектуре: чистое ядро
 * * Тип блока в структурном дизайне: н/а
 * * Слой в чистой архитектуре: юз-кейсы/инфраструктура
 * * Тип блока в Эргономичной архитектуре: контейнер ресурса
 *
 *
 */
data class RoomReservationRequest(
    val hotelId: Int,
    val roomType: RoomType,
    val email: String,
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val from: LocalDate,
    val period: ReservationPeriod
)