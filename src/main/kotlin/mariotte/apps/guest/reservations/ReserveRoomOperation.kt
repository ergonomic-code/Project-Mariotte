package pro.azhidkov.mariotte.apps.guest.reservations

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import pro.azhidkov.mariotte.core.hotels.HotelsService
import pro.azhidkov.mariotte.core.hotels.rooms.RoomType
import pro.azhidkov.mariotte.core.hotels.root.Hotel
import pro.azhidkov.mariotte.core.hotels.root.HotelRef
import pro.azhidkov.mariotte.core.reservations.Reservation
import pro.azhidkov.mariotte.core.reservations.ReservationsRepo
import pro.azhidkov.mariotte.core.reservations.getReservationsAmountPerDate
import pro.azhidkov.mariotte.core.reservations.to
import pro.azhidkov.platform.domain.errors.DomainException
import pro.azhidkov.platform.domain.errors.EntityNotFoundException
import java.time.LocalDate

/**
 * DTO успешного результата операции бронирования
 */
data class ReservationSuccess(
    val reservationId: Int
)

/**
 * Исключения вариантов ошибочного результата бронирования
 */
sealed class RoomReservationException(msg: String) : DomainException(msg)

class NoAvailableRoomsException(hotelRef: HotelRef, roomType: RoomType, from: LocalDate, to: LocalDate) :
    RoomReservationException("There are no available rooms of type: $roomType in hotel $hotelRef from: $from to: $to")

class ReservationDatesInPastException(from: LocalDate) : RoomReservationException("Reservation dates in past: $from")

/**
 * Класс-функция операции, примерный аналог [workflow](https://increment.com/software-architecture/primer-on-functional-architecture/) в терминах Влашина и
 * юз-кейса в терминах чистой архитектуры.
 * * Слой в Функциональной архитектуре: императивная оболочка
 * * Тип блока в структурном дизайне: оркестрация
 * * Слой в чистой архитектуре: юз-кейсы
 * * Тип блока в Эргономичной архитектуре: сложная операция
 *
 * По Эрнономичному подходу операции отвечают за оркестрацию исполнения бизнес-логики.
 * Изначально эти я делал в чистом [ROP-стиле](https://fsharpforfunandprofit.com/rop/),
 * однако со временем по ряду причин (в первую очередь - сложность самой концепции монад и отсутсвия поддержки в Kotlin)
 * отказался от них в пользу [Guard clause](https://refactoring.com/catalog/replaceNestedConditionalWithGuardClauses.html).
 *
 * Тем не менее при кодировании (и ревью) операций я держу в голове метафору железной дороги.
 *
 * Другой моделью системы, повлиявшей на рекомендации по кодированию операций стала [модель, ориентированная на трансформации](https://azhidkov.pro/posts/23/10/why-fp/#_структурный_дизайн).
 * И с ней рекомендации Эргонмичного подхода согласуются в полной мере.
 *
 * На классы реализации операций Эргономичный подход накладывает следующие ограничения:
 * 1. Должны иметь среднее (на усмотрение команды, рекомендуемое значение <= 5) количество зависимостей;
 * 2. Методы должны иметь среднюю (на усмотрение команды, рекомендуемое значение <= 7) цикломатическую сложность;
 * 3. Все эффекты (обращение ресурсам) и возможные исходы (типы ошибок) операции должны быть видны в методе операции.
 *
 * Для обеспечения п. 1 я рекомендую заводить по классу на каждую операцию.
 *
 * Для обеспечения п. 3 я рекомендую все вспомогтаельные функции выносить в верхне-уровневые функции, без доступа к
 * полям класса, а так же явно прописывать поток управления в возможные выходы из функции (а не прятать это
 * внутрь монад, в отличе от ROP-стиля).
 *
 * Все три пункта призваны обеспечить максимальную прозрачность операции для разработчика.
 *
 * Метки в коде:
 * 1. Остатки афферентной ветки/императивной оболочки - трансформируем низкоуровневое представление
 *    ресурса (RoomReservationRequest) в высокоуровневое (Reservation),
 *    все остальные прелести по вычитыванию байт из сокета и т.д. платформа (Tomcat + Spring) забрали на себя
 * 2. Афферентная ветка/императивная оболочка - считываем состояние ресурсов
 * 3. Ветка центральной трансформации/чистое ядро
 * 4. Афферентная ветка/императивная оболочка - считываем состояние ресурсов
 * 5. Синхронизация доступа к ресурсам для обеспечения инварианта
 *    <кол-во резерваций определённого типа в день> <= <кол-во номеров этого типа в отеле> осуществляется посредством
 *    пессимистичных блокировак на уровне БД:
 *    * в начале операция выполняет "SELECT ... FOR UPUDATE" все строки номеров требуемого типа
 *    * если в этот момент начнёт выполняться другая операция, то она будет заблокирована на выборке, до завершения
 *      первой операции
 *    * первая операция принимает решение о возможности бронирования и добавляет резервацию в БД и коммитает транзакцию,
 *      либо откатывает транзакцию
 *    * в этот момент отпускается блокировка, на которой остановлен запрос второй операции и она следующим запросом
 *      вычитывает актуальные брони
 * 6. Ветка центральной трансформации/чистое ядро
 * 7. Эфферентаная ветка/императивная оболочка - обновляем ресурсы
 * 8. Плюс начало остатков эфферентой ветки/императивной оболочки - тут (+ в порте) мы начинаем передавать
 *    результат вызова операции в эфферентую ветку, которая в конце концов завершится записью этого результата
 *    в сетевой сокет
 */
@Component
class ReserveRoomOperation(
    private val reservationsRepo: ReservationsRepo,
    private val hotelsService: HotelsService
) : (RoomReservationRequest) -> ReservationSuccess {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    override operator fun invoke(roomReservationRequest: RoomReservationRequest): ReservationSuccess {
        log.info("Processing room reservation request: {}", roomReservationRequest)

        val reservation = reservationFromRequest(roomReservationRequest) // 1

        val reservationRequestDate = LocalDate.now() // 2

        if (!ReservationRules.canAcceptAt(reservation, reservationRequestDate)) { // 3
            throw ReservationDatesInPastException(reservation.from)
        }

        val hotel = hotelsService.findById(roomReservationRequest.hotelId) // 4
        log.info("Hotel: {}", hotel)
        if (hotel == null) {
            throw EntityNotFoundException(Hotel::class, roomReservationRequest.hotelId)
        }

        val capacity = hotelsService.getCapacityForUpdate(hotel.ref(), reservation.roomType) // 5
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

        return ReservationSuccess(persistedOrder.id) // 8
    }

}

private fun reservationFromRequest(request: RoomReservationRequest): Reservation =
    Reservation(Hotel.ref(request.hotelId), request.roomType, request.email, request.from, request.period)
