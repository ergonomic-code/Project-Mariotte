package pro.azhidkov.mariotte.fixtures.object_mothers

import pro.azhidkov.mariotte.apps.guest.reservations.RoomReservationRq
import pro.azhidkov.mariotte.domain.rooms.RoomType
import pro.azhidkov.mariotte.domain.hotels.HotelRef
import pro.azhidkov.mariotte.domain.reservations.Reservation
import pro.azhidkov.mariotte.domain.reservations.ReservationPeriod
import java.time.LocalDate
import java.time.Period
import java.util.*

/**
 * Фабрика тестовых объектов бронирования.
 *
 * Метки в коде:
 * 1. [ReservationsObjectMother.aRoomReservationRequest] - Обычно я завожу по ObjectMother-у на агрегат и генерацию всех связанных типов (вложенные сущносты, DTO,
 *    представления и т.п.) складываю в него;
 * 2. [ReservationsObjectMother.aReservationPeriod] - Функции генерации доменно-специфичных простых данных я обычно помещаю рядом с ObjectMother-ом агрегата,
 *    который является основным источником/потребителем таких данных.
 */
object ReservationsObjectMother {

    fun aRoomReservationRequest( // 1
        hotelId: UUID = HotelsObjectMother.theHotel.ref.id,
        roomType: RoomType = RoomType.entries.randomElement(),
        email: String = faker.internet().emailAddress(),
        from: LocalDate = nearFutureDate(LocalDate.now()),
        period: ReservationPeriod = aReservationPeriod()
    ) = RoomReservationRq(hotelId, roomType, email, from, period)

    fun aReservation(
        hotelId: HotelRef = HotelsObjectMother.theHotel.ref,
        roomType: RoomType = RoomType.entries.randomElement(),
        email: String = faker.internet().emailAddress(),
        from: LocalDate = nearFutureDate(LocalDate.now()),
        period: ReservationPeriod = aReservationPeriod()
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
        aReservation(hotelId, roomType, from = from, period = period)
    }

    /**
     * Генерация JSON-представления запроса на бронирование номера, которое невозможно
     * представить с помощью класса [RoomReservationRq]
     */
    fun aRoomReservationRequestJson(
        hotelId: HotelRef? = HotelsObjectMother.theHotel.ref,
        roomTypeId: Int? = RoomType.entries.randomElement().id,
        email: String? = faker.internet().emailAddress(),
        from: LocalDate? = nearFutureDate(LocalDate.now()),
        period: Period? = aReservationPeriod().period
    ): String {
        val fieldValues = buildList {
            if (hotelId != null) {
                add(""""hotelId": "${hotelId.id}"""")
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

    fun aReservationPeriod(): ReservationPeriod = // 2
        ReservationPeriod(Period.ofDays(faker.number().numberBetween(1, 14)))

}
