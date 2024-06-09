package pro.azhidkov.mariotte.apps.guest.reservations


import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import pro.azhidkov.mariotte.apps.platform.spring.http.conflictOf
import pro.azhidkov.mariotte.core.reservations.Reservation
import pro.azhidkov.mariotte.core.reservations.ReservationDetails
import pro.azhidkov.mariotte.core.reservations.ReservationsRepo
import pro.azhidkov.platform.domain.errors.EntityNotFoundException
import pro.azhidkov.platform.kotlin.unwrap

/**
 * Порт бронирования номера и просмотра деталей брони.
 * * Слой в Функциональной архитектуре: императивная оболочка
 * * Тип блока в Структурном дизайне: не очевидно, ближе всего оркестрация
 * * Слой в Чистой архитектуре: инфраструктура
 * * Тип блока в Эргономичной архитектуре: порт
 *
 * На данный момент конкретной стратегии разбиения методов обработчиков эндпоинтов по классам вообще нет,
 * а тот рабочий вариант, что есть сейчас берёт за основу либо UI, либо ТЗ, коих у этого проекта нет.
 * И в отсутствие других руководящих принципов, оба метода собраны в одном классе в имя простоты.
 *
 * По Эргономичному подходу порты отвечают за трансляцию внешних обращений в вызовы операций системы и за трансляцию
 * результатов вызова операции во внешние представления.
 *
 * Эргономичный подход накладывает следующие ограничения на реализацию портов:
 * 1. Должны иметь небольшое (на усмотрение команды, рекомендуемое значение <= 4) количество зависимостей
 * 2. Не могут зависеть от других портов
 * 3. Каждый метод порта может содержать не более одного вызова операции или метода ресурса
 * 4. Каждый метод порта может содержать не более одного ветвления, отвечающего за выбор представления результата
 *    обработки запроса
 *
 * При этом, с условием соблюдения ограничений выше, порты могут обращаться к ресурсам напрямую, в обход операций.
 *
 * Метки в коде:
 * 1. Этот паттерн: Операция выбрасывает исключения, а порт вызывает её через runCatching - компромисс.
 *
 *    С одной стороны, Result + when на мой взгляд более нагляден - он позволяет разделить собственно вызов операции
 *    и выбор представления результата. Плюс в целом более лаконичен.
 *
 *    Но Spring-овые транзакции (как и у Exposed, кстати) завязаны на исключения, и если из операций возвращать Result,
 *    то придётся либо в каждой операции руками рулить транзакциями, либо писать собственную @FunctionalTransactional,
 *    которая будет смотреть на значение результата и финалить транзакцию соответствующим образом.
 *
 *    Поэтому я пока выкрутился этим паттерном.
 * 2. Правила маппинга ошибок на коды HTTP описаны в [зачатке гайдлайна Эргономичного подхода](https://github.com/ergonomic-code/Ergo-Approach-Guideline/wiki/Проектирование-HTTP-API#коды-ошибок)
 */
@RestController
@RequestMapping("/guest/reservations")
class ReservationsController(
    private val reserveRoom: ReserveRoomOperation,
    private val reservationsRepo: ReservationsRepo
) {

    @PostMapping
    fun handleReserveRoom(@Valid @RequestBody request: RoomReservationRequest): ResponseEntity<*> {
        val res: Result<ReservationSuccess> = runCatching { reserveRoom((request)) } // 1

        return when (val v = res.unwrap()) {
            is ReservationSuccess -> ok(v)
            is ReservationDatesInPastException -> conflictOf(v) // 2
            is EntityNotFoundException -> conflictOf(v)
            is NoAvailableRoomsException -> conflictOf(v)
            else -> throw (v as Throwable)
        }
    }

    @GetMapping("/{reservationId}")
    fun handleGetReservation(@PathVariable reservationId: Int): ResponseEntity<*> {
        val res = runCatching { reservationsRepo.findDetailsById(reservationId) }

        return when (val v = res.unwrap()) {
            is ReservationDetails -> ok(res)
            null -> conflictOf(EntityNotFoundException(Reservation::class, reservationId))
            else -> throw (v as Throwable)
        }
    }

}