package pro.azhidkov.mariotte.core.reservations

import pro.azhidkov.mariotte.core.hotels.root.Hotel
import java.time.Period


/**
 * Модуль трансформаций для сущности "Бронирование".
 * Чаще всего содержит функции конвретации из различных ДТО в сущность и обратно.
 * Кроме этого может содержать функции вычисления каких-либо данных и конструирования
 * новых версий целевой сущности, которые зависят от дополнительын типов, помимо самой целевой сущности
 * и типов из стандартной библиотеки.
 *
 * * Слой в Функциональной архитектуре: чистое ядро
 * * Тип блока в структурном дизайне: н/а
 * * Слой в чистой архитектуре: Н/а
 * * Тип блока в Эргономичной архитектуре: запись ресурса
 */
object Reservations {

    fun reservationFromRequest(request: RoomReservationRequest): Reservation =
        Reservation(Hotel.ref(request.hotelId), request.roomType, request.email, request.from, request.period)

}