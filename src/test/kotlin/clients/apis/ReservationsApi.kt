package pro.azhidkov.mariotte.clients.apis

import org.springframework.test.web.servlet.client.RestTestClient
import pro.azhidkov.mariotte.apps.guest.reservations.ReservationSuccess
import pro.azhidkov.mariotte.apps.guest.reservations.ReservationsController
import pro.azhidkov.mariotte.apps.guest.reservations.RoomReservationRequest
import pro.azhidkov.mariotte.apps.platform.spring.http.ErrorResponse
import pro.azhidkov.mariotte.assertions.serializeToValidJson
import pro.azhidkov.mariotte.core.reservations.Reservation
import pro.azhidkov.mariotte.platform.spring.test.web.servlet.getBodyMatchingSchema
import tools.jackson.databind.ObjectMapper
import java.util.*

/**
 * Клиент API фичи бронирования приложения Гостя.
 *
 * Он решает две связанные задачи:
 * 1. Инкапсулирует детали выполнения запросов к API (HTTP в целом и HTTP-клиент в частности)
 * 2. Как следствие - убирает весь шум, связанный с HTTP, из кода тест-кейсов и делает их более наглядными
 */
class ReservationsApi(
    private val client: RestTestClient,
    private val objectMapper: ObjectMapper
) {

    fun reserveRoom(roomReservationRequest: RoomReservationRequest): ReservationSuccess {
        val body = objectMapper.serializeToValidJson(
            roomReservationRequest, GuestJsonSchemas.Reservations.ROOM_RESERVATION_REQUEST
        )

        return reserveRoomForResponse(body)
            .expectStatus().isCreated
            .getBodyMatchingSchema(schema = GuestJsonSchemas.Reservations.ROOM_RESERVATION_RESPONSE)
    }

    fun reserveRoomForError(roomReservationRequest: RoomReservationRequest): ErrorResponse {
        val body = objectMapper.serializeToValidJson(
            roomReservationRequest, GuestJsonSchemas.Reservations.ROOM_RESERVATION_REQUEST
        )

        return reserveRoomForError(body)
    }

    fun reserveRoomForError(requestBody: String): ErrorResponse {
        return reserveRoomForResponse(requestBody)
            .getBodyMatchingSchema(SharedJsonSchemas.ERROR_RESPONSE)
    }

    private fun reserveRoomForResponse(body: String): RestTestClient.ResponseSpec = client
        .post()
        .uri(ReservationsController.RESERVE_ROOM)
        .body(body)
        .exchange()

    fun getReservation(reservationId: UUID): Reservation {
        return client
            .get()
            .uri(ReservationsController.RESERVATION_DETAILS, reservationId)
            .exchange()
            .expectStatus().isOk
            .getBodyMatchingSchema(schema = GuestJsonSchemas.Reservations.RESERVATION_RESPONSE)
    }

}
