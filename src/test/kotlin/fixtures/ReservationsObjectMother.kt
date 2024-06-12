package pro.azhidkov.mariotte.fixtures

import pro.azhidkov.mariotte.apps.guest.reservations.RoomReservationRequest
import pro.azhidkov.mariotte.core.hotels.rooms.RoomType
import pro.azhidkov.mariotte.core.hotels.root.Hotel
import pro.azhidkov.mariotte.core.hotels.root.HotelRef
import pro.azhidkov.mariotte.core.reservations.Reservation
import pro.azhidkov.mariotte.core.reservations.ReservationPeriod
import java.time.LocalDate
import java.time.Period

/**
 * Фабрика тестовых объектов бронирования.
 *
 * Метки в коде:
 * 1. [ReservationsObjectMother.roomReservationRequest] - Обычно я завожу по ObjectMother-у на агрегат и генерацию всех связанных типов (вложенные сущносты, DTO,
 *    представления и т.п.) складываю в него;
 * 2. [ReservationsObjectMother.randomReservationPeriod] - Функции генерации доменно-специфичных простых данных я обычно помещаю рядом с ObjectMother-ом агрегата,
 *    который является основным источником/потребителем таких данных.
 */
object ReservationsObjectMother {

    fun roomReservationRequest( // 1
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

    /**
     * Фабрика фабрик конкурирующих бронирований.
     * Бронирования являются конкурирующими, если они "целятся" в один и тот же тип номера в одном отеле.
     */
    fun concurrentReservations(
        hotelId: HotelRef = HotelsObjectMother.theHotel.ref,
        roomType: RoomType = RoomType.entries.randomElement(),
    ): (LocalDate, ReservationPeriod) -> Reservation = { from: LocalDate, period: ReservationPeriod ->
        reservation(hotelId, roomType, from = from, period = period)
    }

    /**
     * Генерация JSON-представления запроса на бронирование номера, которое невозможно
     * представить с помощью класса [RoomReservationRequest]
     */
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

    fun randomReservationPeriod(): ReservationPeriod = // 2
        ReservationPeriod(Period.ofDays(random.nextInt(1, 14)))

}

