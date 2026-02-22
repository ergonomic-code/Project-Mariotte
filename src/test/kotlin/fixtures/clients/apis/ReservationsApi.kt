package pro.azhidkov.mariotte.fixtures.clients.apis

import org.springframework.test.web.servlet.client.RestTestClient
import pro.azhidkov.mariotte.apps.guest.reservations.ReservationSuccessRs
import pro.azhidkov.mariotte.apps.guest.reservations.ReservationsController
import pro.azhidkov.mariotte.apps.guest.reservations.RoomReservationRq
import pro.azhidkov.mariotte.apps.platform.spring.http.ErrorResponse
import pro.azhidkov.mariotte.assertions.assertJsonMatchesSchema
import pro.azhidkov.mariotte.assertions.serializeToValidJson
import pro.azhidkov.mariotte.core.reservations.ReservationDetailsView
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

    fun reserveRoom(roomReservationRq: RoomReservationRq): ReservationSuccessRs {
        val body = serializeRoomReservationRequest(roomReservationRq)

        return reserveRoomForResponse(body)
            .expectStatus().isCreated
            .getBodyMatchingSchema(schema = GuestJsonSchemas.Reservations.ROOM_RESERVATION_RESPONSE)
    }

    fun reserveRoomForError(roomReservationRq: RoomReservationRq): ErrorResponse {
        val body = serializeRoomReservationRequest(roomReservationRq)

        return reserveRoomForError(body)
    }

    fun reserveRoomForError(requestBody: String): ErrorResponse {
        return reserveRoomForResponse(requestBody)
            .getBodyMatchingSchema(SharedJsonSchemas.ERROR_RESPONSE)
    }

    fun reserveRoomForOutcome(
        roomReservationRq: RoomReservationRq,
        expectedErrors: Set<ExpectedHttpError>
    ): HttpOutcome<ReservationSuccessRs, ErrorResponse> {
        val body = serializeRoomReservationRequest(roomReservationRq)
        val responseBody = reserveRoomForResponse(body)
            .expectBody()
            .returnResult()
            .responseBody
            ?.let { String(it) }
            ?: throw AssertionError("Expected non-empty response body for reserve room request")

        if (responseBody.matchesSchema(GuestJsonSchemas.Reservations.ROOM_RESERVATION_RESPONSE)) {
            val success = objectMapper.readValue(responseBody, ReservationSuccessRs::class.java)
            return HttpOutcome.Success(success)
        }

        if (!responseBody.matchesSchema(SharedJsonSchemas.ERROR_RESPONSE)) {
            throw AssertionError(
                "Response body does not match success or error schema for reserve room request: $responseBody"
            )
        }

        val error = objectMapper.readValue(responseBody, ErrorResponse::class.java)
        val actualError = ExpectedHttpError(error.status, error.type?.path)
        if (actualError !in expectedErrors) {
            throw AssertionError("Unexpected error response for reserve room request: $actualError")
        }
        return HttpOutcome.ExpectedError(error)
    }

    private fun reserveRoomForResponse(body: String): RestTestClient.ResponseSpec = client
        .post()
        .uri(ReservationsController.RESERVE_ROOM)
        .body(body)
        .exchange()

    fun getReservation(reservationId: UUID): ReservationDetailsView {
        return client
            .get()
            .uri(ReservationsController.RESERVATION_DETAILS, reservationId)
            .exchange()
            .expectStatus().isOk
            .getBodyMatchingSchema(schema = GuestJsonSchemas.Reservations.RESERVATION_RESPONSE)
    }

    fun getReservationForError(reservationId: UUID): ErrorResponse {
        return client
            .get()
            .uri(ReservationsController.RESERVATION_DETAILS, reservationId)
            .exchange()
            .getBodyMatchingSchema(SharedJsonSchemas.ERROR_RESPONSE)
    }

    private fun serializeRoomReservationRequest(roomReservationRq: RoomReservationRq): String {
        return objectMapper.serializeToValidJson(
            roomReservationRq,
            GuestJsonSchemas.Reservations.ROOM_RESERVATION_REQUEST
        )
    }

}

private fun String.matchesSchema(schema: String): Boolean {
    return runCatching { assertJsonMatchesSchema(this, schema) }.isSuccess
}
