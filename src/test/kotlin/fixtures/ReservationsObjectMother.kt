package pro.azhidkov.mariotte.fixtures

import pro.azhidkov.mariotte.apps.guest.reservations.RoomReservationRequest
import pro.azhidkov.mariotte.core.hotels.rooms.RoomType
import pro.azhidkov.mariotte.core.hotels.root.Hotel
import pro.azhidkov.mariotte.core.hotels.root.HotelRef
import pro.azhidkov.mariotte.core.reservations.Reservation
import pro.azhidkov.mariotte.core.reservations.ReservationPeriod
import java.time.LocalDate
import java.time.Period


object ReservationsObjectMother {

    fun roomReservationRequest(
        hotelId: Int = randomIntId(),
        roomType: RoomType = RoomType.entries.randomElement(),
        email: String = faker.internet().emailAddress(),
        from: LocalDate = nearFutureDate(LocalDate.now()),
        period: ReservationPeriod = randomReservationPeriod()
    ) = RoomReservationRequest(hotelId, roomType, email, from, period)

    fun reservation(
        hotelId: HotelRef = Hotel.ref(randomIntId()),
        roomType: RoomType = RoomType.entries.randomElement(),
        email: String = faker.internet().emailAddress(),
        from: LocalDate = nearFutureDate(LocalDate.now()),
        period: ReservationPeriod = randomReservationPeriod()
    ): Reservation =
        Reservation(hotelId, roomType, email, from = from, period = period)

    fun concurrentReservations(
        hotelId: HotelRef = HotelsObjectMother.theHotel.ref,
        roomType: RoomType = RoomType.entries.randomElement(),
    ): (LocalDate, ReservationPeriod) -> Reservation = { from: LocalDate, period: ReservationPeriod ->
        reservation(hotelId, roomType, from = from, period = period)
    }

    fun createRoomReservationRequestJson(
        hotelId: HotelRef? = Hotel.ref(randomIntId()),
        roomTypeId: Int? = RoomType.entries.randomElement().id,
        email: String? = faker.internet().emailAddress(),
        from: LocalDate? = nearFutureDate(LocalDate.now()),
        period: Period? = randomReservationPeriod().period
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
            if (period != null) {
                add(""""period": "$period"""")
            }
        }

        return fieldValues.joinToString(",", "{", "}")
    }
}