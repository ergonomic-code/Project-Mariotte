package pro.azhidkov.mariotte.fixtures

import pro.azhidkov.mariotte.core.hotels.rooms.RoomType
import pro.azhidkov.mariotte.core.hotels.root.Hotel
import pro.azhidkov.mariotte.core.hotels.root.HotelRef
import pro.azhidkov.mariotte.core.reservations.Reservation
import pro.azhidkov.mariotte.core.reservations.Reservations
import pro.azhidkov.mariotte.core.reservations.RoomReservationRequest
import java.time.LocalDate


object ReservationsObjectMother {

    fun roomReservationRequest(
        hotelId: Int = randomIntId(),
        roomType: RoomType = RoomType.entries.randomElement(),
        email: String = faker.internet().emailAddress(),
        from: LocalDate = nearFutureDate(LocalDate.now()),
        to: LocalDate = from.plusDays(randomReservationDuration())
    ) = RoomReservationRequest(hotelId, roomType, email, from, to)

    fun reservation(
        hotelId: HotelRef = Hotel.ref(randomIntId()),
        roomType: RoomType = RoomType.entries.randomElement(),
        email: String = faker.internet().emailAddress(),
        from: LocalDate = nearFutureDate(LocalDate.now()),
        to: LocalDate = from.plusDays(randomReservationDuration())
    ): Reservation =
        Reservations.reservationFromRequest(
            roomReservationRequest(
                hotelId.id!!,
                roomType,
                email,
                from = from,
                to = to
            ), LocalDate.now()
        ).getOrThrow()

    fun concurrentReservations(
        hotelId: HotelRef = HotelsObjectMother.theHotel(),
        roomType: RoomType = RoomType.entries.randomElement(),
    ) = { from: LocalDate, to: LocalDate ->
        reservation(hotelId, roomType, from = from, to = to)
    }

    fun createRoomReservationRequestJson(
        hotelId: HotelRef? = Hotel.ref(randomIntId()),
        roomTypeId: Int? = RoomType.entries.randomElement().id,
        email: String? = faker.internet().emailAddress(),
        from: LocalDate? = nearFutureDate(LocalDate.now()),
        to: LocalDate? = from?.plusDays(randomReservationDuration())
    ): String {
        val fieldValues = buildList {
            if (hotelId != null) {
                add(""""hotel": ${hotelId.id}""")
            }
            if (roomTypeId != null) {
                add(""""roomType": $roomTypeId""")
            }
            if (email != null) {
                add(""""email": "$email"""")
            }
            if (from != null) {
                add(""""from": "$from"""")
            }
            if (to != null) {
                add(""""to": "$to"""")
            }
        }

        return fieldValues.joinToString(",", "{", "}")
    }
}