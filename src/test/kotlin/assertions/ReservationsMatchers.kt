package pro.azhidkov.mariotte.assertions


import io.kotest.matchers.shouldBe
import pro.azhidkov.mariotte.apps.guest.reservations.RoomReservationRq
import pro.azhidkov.mariotte.core.reservations.Reservation

/**
 * Доменно специфичный матчер для проверки того, что сущность бронирования соответствует ДТО запроса.
 *
 * Такие матчеры предназначены в первую очередь для того, чтобы поддерживать высокий уровень абстракции и лаконичность
 * кода тест-кейсов.
 */
infix fun Reservation.shouldMatch(roomReservationRq: RoomReservationRq) {
    this.hotel.id shouldBe roomReservationRq.hotelId
    this.roomType shouldBe roomReservationRq.roomType
    this.from shouldBe roomReservationRq.from
    this.period shouldBe roomReservationRq.period
    this.email shouldBe roomReservationRq.email
}
