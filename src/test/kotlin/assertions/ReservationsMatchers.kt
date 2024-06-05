package pro.azhidkov.mariotte.assertions


import io.kotest.matchers.shouldBe
import pro.azhidkov.mariotte.apps.guest.reservations.RoomReservationRequest
import pro.azhidkov.mariotte.core.reservations.Reservation

infix fun Reservation.shouldMatch(roomReservationRequest: RoomReservationRequest) {
    this.hotel.id shouldBe roomReservationRequest.hotelId
    this.roomType shouldBe roomReservationRequest.roomType
    this.from shouldBe roomReservationRequest.from
    this.period shouldBe roomReservationRequest.period
    this.email shouldBe roomReservationRequest.email
}