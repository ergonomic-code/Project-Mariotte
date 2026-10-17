package pro.azhidkov.mariotte.fixtures.clients.apis

import com.networknt.schema.Schema
import org.springframework.test.web.servlet.client.RestTestClient
import org.springframework.test.web.servlet.client.expectBody
import pro.azhidkov.mariotte.app.platform.spring.http.ErrorResponse
import pro.azhidkov.mariotte.app.reservations.ReservationSuccessRs
import pro.azhidkov.mariotte.app.reservations.ReservationsController
import pro.azhidkov.mariotte.app.reservations.RoomReservationRq
import pro.azhidkov.mariotte.assertions.assertJsonMatchesSchema
import pro.azhidkov.mariotte.domain.reservations.ReservationDetailsView
import pro.azhidkov.mariotte.fixtures.clients.ApiSession
import pro.azhidkov.mariotte.fixtures.clients.Guest
import pro.azhidkov.mariotte.fixtures.clients.apis.ReservationsSchemas.reservationResponseSchema
import pro.azhidkov.mariotte.fixtures.clients.apis.ReservationsSchemas.roomReservationRequestSchema
import pro.azhidkov.mariotte.fixtures.clients.apis.ReservationsSchemas.roomReservationResponseSchema
import pro.azhidkov.mariotte.fixtures.clients.apis.SharedJsonSchemas.errorResponseSchema
import pro.azhidkov.mariotte.platform.spring.test.web.servlet.ensureBodyMatchesSchema
import pro.azhidkov.mariotte.platform.spring.test.web.servlet.getBodyMatchingSchema
import java.util.*

val Guest.reservationsApi: ReservationsHttpApi
    get() = ReservationsHttpApi(this)

/**
 * Клиент API фичи бронирования приложения Гостя.
 *
 * Он решает две связанные задачи:
 * 1. Инкапсулирует детали выполнения запросов к API (HTTP в целом и HTTP-клиент в частности)
 * 2. Как следствие - убирает весь шум, связанный с HTTP, из кода тест-кейсов и делает их более наглядными
 */
class ReservationsHttpApi(
    private val session: ApiSession,
) {

    fun reserveRoom(roomReservationRq: RoomReservationRq): ReservationSuccessRs {
        return reserveRoomForResponse(roomReservationRq)
            .expectStatus().isCreated
            .getBodyMatchingSchema(schema = roomReservationResponseSchema)
    }

    fun reserveRoomForError(roomReservationRq: RoomReservationRq): ErrorResponse {
        return reserveRoomForResponse(roomReservationRq)
            .getBodyMatchingSchema(errorResponseSchema)
    }

    fun reserveRoomForError(requestBody: String): ErrorResponse {
        return reserveRoomForResponse(requestBody)
            .getBodyMatchingSchema(errorResponseSchema)
    }

    fun reserveRoomForOutcome(
        roomReservationRq: RoomReservationRq,
        expectedErrors: Set<ExpectedHttpError>
    ): HttpOutcome<ReservationSuccessRs, ErrorResponse> {
        val response = reserveRoomForResponse(roomReservationRq)
        val responseBody = response
            .expectBody()
            .returnResult()
            .responseBody
            ?.let { String(it) }
            ?: throw AssertionError("Expected non-empty response body for reserve room request")

        val successSchemaValidation = responseBody.validateAgainstSchema(roomReservationResponseSchema)
        if (successSchemaValidation.isSuccess) {
            val success = response.expectBody<ReservationSuccessRs>().returnResult().responseBody!!
            return HttpOutcome.Success(success)
        }

        val errorSchemaValidation = responseBody.validateAgainstSchema(errorResponseSchema)
        if (errorSchemaValidation.isFailure) {
            throw AssertionError(
                "Response body does not match success or error schema for reserve room request: $responseBody"
            ).also { assertion ->
                successSchemaValidation.exceptionOrNull()?.let(assertion::addSuppressed)
                errorSchemaValidation.exceptionOrNull()?.let(assertion::addSuppressed)
            }
        }

        val error = response.expectBody<ErrorResponse>().returnResult().responseBody!!
        val actualError = ExpectedHttpError(error.status, error.type?.path)
        if (actualError !in expectedErrors) {
            throw AssertionError("Unexpected error response for reserve room request: $actualError")
        }
        return HttpOutcome.ExpectedError(error)
    }

    private fun reserveRoomForResponse(body: Any): RestTestClient.ResponseSpec {
        val request = session.restClient.post()
            .uri(ReservationsController.RESERVE_ROOM)
            .body(body)
        if (body is RoomReservationRq) {
            request.ensureBodyMatchesSchema(roomReservationRequestSchema)
        }
        return request.exchange()
    }

    fun getReservation(reservationId: UUID): ReservationDetailsView {
        return session.restClient
            .get()
            .uri(ReservationsController.RESERVATION_DETAILS, reservationId)
            .exchange()
            .expectStatus().isOk
            .getBodyMatchingSchema(schema = reservationResponseSchema)
    }

    fun getReservationForError(reservationId: UUID): ErrorResponse {
        return session.restClient
            .get()
            .uri(ReservationsController.RESERVATION_DETAILS, reservationId)
            .exchange()
            .getBodyMatchingSchema(errorResponseSchema)
    }

}

private fun String.validateAgainstSchema(schema: Schema): Result<Unit> {
    return runCatching { assertJsonMatchesSchema(this, schema) }
}
