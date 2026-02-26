package pro.azhidkov.mariotte.app.reservations


import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import pro.azhidkov.mariotte.app.platform.spring.http.conflictOf
import pro.azhidkov.mariotte.app.platform.spring.http.created
import pro.azhidkov.mariotte.app.platform.spring.http.notFoundOf
import pro.azhidkov.mariotte.app.platform.spring.http.unprocessableEntityOf
import pro.azhidkov.mariotte.domain.reservations.Reservation
import pro.azhidkov.mariotte.domain.reservations.ReservationDetailsView
import pro.azhidkov.mariotte.domain.reservations.ReservationsRepo
import pro.azhidkov.platform.domain.errors.EntityNotFoundException
import pro.azhidkov.platform.kotlin.unwrap
import java.util.*

/**
 * Порт бронирования номера и просмотра деталей брони.
 *
 * По Эргономичному подходу порты отвечают за трансляцию внешних обращений в вызовы операций системы
 * и за маппинг результатов во внешние представления.
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
 * 2. Правила маппинга ошибок на коды HTTP описаны в [гайдлайне Эргономичного подхода](https://ergowiki.azhidkov.pro/docs/patterns/http-json-api/status-code-choosing/)
 * 3. Я осознанно дублирую префиксы отдельных эндпоинтов, потому что это упрощает их поиск в кодовой базе и, как следствие,
 *    нивелирует проблемы вызванные дублированием.
 */
@RestController
class ReservationsController(
    private val reserveRoom: ReserveRoomOp,
    private val reservationsRepo: ReservationsRepo
) {

    @PostMapping(RESERVE_ROOM)
    fun handleReserveRoom(@Valid @RequestBody request: RoomReservationRq): ResponseEntity<*> {
        val res: Result<ReservationSuccessRs> = runCatching { reserveRoom((request)) } // 1

        return when (val v = res.unwrap()) {
            is ReservationSuccessRs -> created(v)
            is ReservationDatesInPastException -> unprocessableEntityOf(v) // 2
            is EntityNotFoundException -> notFoundOf(v)
            is NoAvailableRoomsException -> conflictOf(v)
            else -> throw (v as Throwable)
        }
    }

    @GetMapping(RESERVATION_DETAILS)
    fun handleGetReservationDetails(@PathVariable reservationId: UUID): ResponseEntity<*> {
        val res = runCatching { reservationsRepo.findDetailsById(reservationId) }

        return when (val v = res.unwrap()) {
            is ReservationDetailsView -> ok(v)
            null -> notFoundOf(EntityNotFoundException(Reservation::id, reservationId))
            else -> throw (v as Throwable)
        }
    }

    companion object {
        const val RESERVE_ROOM = "/reservations"
        const val RESERVATION_DETAILS = "/reservations/{reservationId}" // 3
    }

}
