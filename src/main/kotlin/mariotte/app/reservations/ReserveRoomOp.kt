package pro.azhidkov.mariotte.app.reservations

import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import pro.azhidkov.mariotte.domain.hotels.Hotel
import pro.azhidkov.mariotte.domain.hotels.HotelRef
import pro.azhidkov.mariotte.domain.hotels.HotelsRepo
import pro.azhidkov.mariotte.domain.reservations.Reservation
import pro.azhidkov.mariotte.domain.reservations.ReservationsRepo
import pro.azhidkov.mariotte.domain.reservations.to
import pro.azhidkov.mariotte.domain.rooms.RoomType
import pro.azhidkov.mariotte.domain.rooms.RoomsRepo
import pro.azhidkov.platform.domain.errors.DomainException
import pro.azhidkov.platform.domain.errors.EntityNotFoundException
import java.time.LocalDate
import java.util.*

/**
 * DTO успешного результата операции бронирования.
 */
data class ReservationSuccessRs(
    val reservationId: UUID
)

/**
 * Базовый тип ожидаемых ошибок операции бронирования.
 */
sealed class RoomReservationException(msg: String) : DomainException(msg)

class NoAvailableRoomsException(hotelRef: HotelRef, roomType: RoomType, from: LocalDate, to: LocalDate) :
    RoomReservationException("There are no available rooms of type: $roomType in hotel $hotelRef from: $from to: $to") {
    companion object {
        const val CODE = "no-available-rooms"
    }
}

class ReservationDatesInPastException(from: LocalDate) : RoomReservationException("Reservation dates in past: $from") {
    companion object {
        const val CODE = "reservation-dates-in-past"
    }
}

/**
 * Класс-функция операции, примерный аналог [workflow](https://increment.com/software-architecture/primer-on-functional-architecture/) в терминах Влашина и
 * юз-кейса в терминах чистой архитектуры.
 *
 * По Эргономичному подходу операции отвечают за оркестрацию исполнения бизнес-логики.
 * Изначально я их делал в чистом [ROP-стиле](https://fsharpforfunandprofit.com/rop/),
 * однако со временем по ряду причин (в первую очередь - сложность самой концепции монад и отсутствия поддержки в Kotlin)
 * отказался от них в пользу [Guard clause](https://refactoring.com/catalog/replaceNestedConditionalWithGuardClauses.html).
 *
 * Тем не менее при кодировании (и ревью) операций я держу в голове метафору железной дороги.
 *
 * Другой моделью системы, повлиявшей на рекомендации по кодированию операций стала [модель, ориентированная на трансформации](https://azhidkov.pro/posts/24/11/structured-design/#_экспресс_курс_структурного_дизайна).
 * И с ней рекомендации Эргономичного подхода согласуются в полной мере.
 *
 * На классы реализации операций Эргономичный подход накладывает следующие ограничения:
 * 1. Должны иметь среднее (на усмотрение команды, рекомендуемое значение <= 5) количество зависимостей;
 * 2. Методы должны иметь среднюю (на усмотрение команды, рекомендуемое значение <= 7) цикломатическую сложность;
 * 3. Все эффекты значимые для конечного пользователя эффекты и возможные исходы (типы ошибок) выполнения операции должны быть видны в методе операции.
 *
 * Для обеспечения п. 1 я рекомендую заводить по классу на каждую операцию.
 *
 * Для обеспечения п. 3 я рекомендую все вспомогательные функции выносить в верхнеуровневые функции без доступа к
 * полям класса, а также явно прописывать поток управления в возможные выходы из функции (а не прятать это
 * внутрь монад, в отличие от ROP-стиля).
 *
 * Все три пункта призваны обеспечить максимальную прозрачность операции для разработчика.
 *
 * Метки в коде:
 * 1. Остатки афферентной ветки/императивной оболочки - трансформируем низкоуровневое представление
 *    ресурса (RoomReservationRq) в высокоуровневое (Reservation),
 *    все остальные прелести по вычитыванию байт из сокета и т.д. платформа (Tomcat + Spring) забрали на себя
 * 2. Афферентная ветка/императивная оболочка - считываем состояние ресурсов
 * 3. Ветка центральной трансформации/чистое ядро
 * 4. Афферентная ветка/императивная оболочка - считываем состояние ресурсов
 * 5. Синхронизация доступа к ресурсам для обеспечения инварианта
 *    <кол-во резерваций определённого типа в день> <= <кол-во номеров этого типа в отеле> осуществляется посредством
 *    пессимистичных блокировок на уровне БД:
 *    * в начале операция выполняет "SELECT ... FOR UPDATE" по строкам номеров требуемого типа
 *    * если в этот момент начнёт выполняться другая операция, то она будет заблокирована на выборке, до завершения
 *      первой операции
 *    * первая операция принимает решение о возможности бронирования, добавляет резервацию в БД и коммитит транзакцию,
 *      либо откатывает транзакцию
 *    * в этот момент отпускается блокировка, на которой остановлен запрос второй операции и она следующим запросом
 *      вычитывает актуальные брони
 * 6. Ветка центральной трансформации/чистое ядро
 * 7. Эфферентная ветка/императивная оболочка - обновляем ресурсы
 * 8. Плюс начало остатка эфферентной ветки/императивной оболочки - тут (+ в порте) мы начинаем передавать
 *    результат вызова операции в эфферентную ветку, которая в конце концов завершится записью этого результата
 *    в сетевой сокет
 */
@Component
class ReserveRoomOp(
    private val reservationsRepo: ReservationsRepo,
    private val hotelsRepo: HotelsRepo,
    private val roomsRepo: RoomsRepo
) : (RoomReservationRq) -> ReservationSuccessRs {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    override operator fun invoke(roomReservationRq: RoomReservationRq): ReservationSuccessRs {
        log.info("Processing room reservation request: {}", roomReservationRq)

        val reservation = reservationFromRequest(roomReservationRq) // 1

        val reservationRequestDate = LocalDate.now() // 2

        if (!ReservationRules.canAcceptAt(reservation, reservationRequestDate)) { // 3
            throw ReservationDatesInPastException(reservation.from)
        }

        val hotel = hotelsRepo.findByIdOrNull(roomReservationRq.hotelId) // 4
        log.info("Hotel: {}", hotel)
        if (hotel == null) {
            throw EntityNotFoundException(Hotel::id, roomReservationRq.hotelId)
        }

        val capacity = roomsRepo.getCapacityForUpdate(hotel.ref(), reservation.roomType) // 5
        log.info("Capacity: {}", capacity)
        if (capacity == null) {
            throw EntityNotFoundException(RoomType::class, hotel to reservation.roomType)
        }

        val reservationsByDate =
            reservationsRepo.getReservationsAmountPerDate(
                hotel.ref(),
                reservation.roomType,
                reservation.from,
                reservation.to
            )
        log.info("Reservations by date: {}", reservationsByDate)

        if (!ReservationRules.hasRequiredFreeRooms(capacity, reservationsByDate)) { // 6
            throw NoAvailableRoomsException(hotel.ref(), reservation.roomType, reservation.from, reservation.to)
        }

        val persistedOrder = reservationsRepo.save(reservation) // 7

        return ReservationSuccessRs(persistedOrder.id) // 8
    }

}

private fun reservationFromRequest(request: RoomReservationRq): Reservation =
    Reservation(Hotel.ref(request.hotelId), request.roomType, request.email, request.from, request.period)
