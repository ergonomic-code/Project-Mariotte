package pro.azhidkov.mariotte.assertions


import io.kotest.matchers.shouldBe
import pro.azhidkov.mariotte.core.reservations.Reservation
import pro.azhidkov.mariotte.core.reservations.RoomReservationRequest

infix fun Reservation.shouldMatch(roomReservationRequest: RoomReservationRequest) {
    this.hotelRef.id shouldBe roomReservationRequest.hotelId
    this.roomType shouldBe roomReservationRequest.roomType
    this.from shouldBe  roomReservationRequest.from
    this.to shouldBe  roomReservationRequest.to
    this.email shouldBe  roomReservationRequest.email
}