package pro.azhidkov.mariotte.clients.apis

import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath
import org.hamcrest.MatcherAssert.assertThat
import org.springframework.test.web.reactive.server.WebTestClient
import pro.azhidkov.mariotte.apps.guest.reservations.ReservationSuccess
import pro.azhidkov.mariotte.apps.guest.reservations.ReservationsController
import pro.azhidkov.mariotte.apps.guest.reservations.RoomReservationRequest
import pro.azhidkov.mariotte.apps.platform.spring.http.ErrorResponse
import pro.azhidkov.mariotte.assertions.serializeToValidJson
import pro.azhidkov.mariotte.core.reservations.Reservation

/**
 * Клиент API фичи бронирования приложения Гостя.
 *
 * Он решает две связанные задачи:
 * 1. Инкапсулирует детали выполнения запросов к API (HTTP в целом и HTTP-клиент в частности)
 * 2. Как следствие - убирает весь шум, связанный с HTTP, из кода тест-кейсов и делает их более наглядными
 */
class ReservationsApi(
    private val client: WebTestClient, private val objectMapper: ObjectMapper
) {

    fun reserveRoom(roomReservationRequest: RoomReservationRequest): ReservationSuccess {
        val body = objectMapper.serializeToValidJson(
            roomReservationRequest, GuestJsonSchemas.Reservations.ROOM_RESERVATION_REQUEST
        )

        lateinit var res: ReservationSuccess
        client.post().uri(ReservationsController.RESERVE_ROOM).bodyValue(body).exchange()
            .expectStatus().isCreated.expectBody().consumeWith {
                val responseBody = String(it.responseBody!!)
                assertThat(
                    responseBody, matchesJsonSchemaInClasspath(GuestJsonSchemas.Reservations.ROOM_RESERVATION_RESPONSE)
                )
                res = objectMapper.readValue(responseBody, ReservationSuccess::class.java)
            }
        return res
    }

    fun reserveRoomForError(roomReservationRequest: RoomReservationRequest): ErrorResponse {
        val body = objectMapper.serializeToValidJson(
            roomReservationRequest, GuestJsonSchemas.Reservations.ROOM_RESERVATION_REQUEST
        )

        return reserveRoomForError(body)
    }

    fun reserveRoomForError(requestBody: String): ErrorResponse {
        lateinit var res: ErrorResponse
        client.post().uri(ReservationsController.RESERVE_ROOM).bodyValue(requestBody).exchange().expectBody()
            .consumeWith {
                val responseBody = String(it.responseBody!!)
                assertThat(responseBody, matchesJsonSchemaInClasspath(SharedJsonSchemas.ERROR_RESPONSE))
                res = objectMapper.readValue(responseBody, ErrorResponse::class.java)
            }
        return res
    }

    fun getReservation(reservationId: Int): Reservation {
        lateinit var res: Reservation
        client.get().uri(ReservationsController.RESERVATION_DETAILS, reservationId).exchange()
            .expectStatus().isOk.expectBody().consumeWith {
                val responseBody = String(it.responseBody!!)
                assertThat(
                    responseBody, matchesJsonSchemaInClasspath(GuestJsonSchemas.Reservations.RESERVATION_RESPONSE)
                )
                res = objectMapper.readValue(responseBody, Reservation::class.java)
            }
        return res
    }

}
