package pro.azhidkov.mariotte.clients.apis

import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath
import org.hamcrest.MatcherAssert.assertThat
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient
import pro.azhidkov.mariotte.apps.guest.reservations.ReservationSuccess
import pro.azhidkov.mariotte.apps.guest.reservations.RoomReservationRequest
import pro.azhidkov.mariotte.apps.platform.spring.http.ErrorResponse
import pro.azhidkov.mariotte.assertions.serializeToValidJson
import pro.azhidkov.mariotte.core.reservations.Reservation

class ReservationsApi(
    private val client: WebTestClient,
    private val objectMapper: ObjectMapper
) {

    fun reserveRoom(roomReservationRequest: RoomReservationRequest): ReservationSuccess {
        val body =
            objectMapper.serializeToValidJson(roomReservationRequest, Guest.Reservations.ROOM_RESERVATION_REQUEST)

        lateinit var res: ReservationSuccess
        client.post()
            .uri("/guest/reservations")
            .bodyValue(body)
            .exchange()
            .expectStatus().isOk
            .expectBody().consumeWith {
                val responseBody = String(it.responseBody!!)
                assertThat(responseBody, matchesJsonSchemaInClasspath(Guest.Reservations.ROOM_RESERVATION_RESPONSE))
                res = objectMapper.readValue(responseBody, ReservationSuccess::class.java)
            }
        return res
    }

    fun reserveRoomForError(roomReservationRequest: RoomReservationRequest, expectedStatus: HttpStatus): ErrorResponse {
        val body =
            objectMapper.serializeToValidJson(roomReservationRequest, Guest.Reservations.ROOM_RESERVATION_REQUEST)

        lateinit var res: ErrorResponse
        client.post()
            .uri("/guest/reservations")
            .bodyValue(body)
            .exchange()
            .expectStatus().isEqualTo(expectedStatus.value())
            .expectBody().consumeWith {
                val responseBody = String(it.responseBody!!)
                assertThat(responseBody, matchesJsonSchemaInClasspath(Shared.ERROR_RESPONSE))
                res = objectMapper.readValue(responseBody, ErrorResponse::class.java)
            }
        return res
    }

    fun reserveRoomForError(requestBody: String): WebTestClient.ResponseSpec {
        return client.post()
            .uri("/guest/reservations")
            .bodyValue(requestBody)
            .exchange()
    }

    fun getReservation(reservationId: Int): Reservation {
        lateinit var res: Reservation
        client.get()
            .uri("/guest/reservations/{reservationId}", reservationId)
            .exchange()
            .expectStatus().isOk
            .expectBody().consumeWith {
                val responseBody = String(it.responseBody!!)
                assertThat(responseBody, matchesJsonSchemaInClasspath(Guest.Reservations.RESERVATION_RESPONSE))
                res = objectMapper.readValue(responseBody, Reservation::class.java)
            }
        return res
    }

}
