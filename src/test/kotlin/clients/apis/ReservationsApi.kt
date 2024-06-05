package pro.azhidkov.mariotte.clients.apis

import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import org.springframework.http.HttpStatus
import pro.azhidkov.mariotte.apps.guest.reservations.ReservationSuccess
import pro.azhidkov.mariotte.apps.guest.reservations.RoomReservationRequest
import pro.azhidkov.mariotte.apps.platform.spring.http.ErrorResponse
import pro.azhidkov.mariotte.assertions.HttpClientAssertions
import pro.azhidkov.mariotte.core.reservations.Reservation

class ReservationsApi(
    override val objectMapper: ObjectMapper
) : HttpClientAssertions {

    fun reserveRoom(roomReservationRequest: RoomReservationRequest): ReservationSuccess {
        val body = assertBodyMatchesSchema(roomReservationRequest, Guest.Reservations.ROOM_RESERVATION_REQUEST)

        return Given {
            body(body)
        } When {
            post("/guest/reservations")
        } Then {
            statusCode(HttpStatus.OK.value())
            body(matchesJsonSchemaInClasspath(Guest.Reservations.ROOM_RESERVATION_RESPONSE))
        } Extract {
            `as`(ReservationSuccess::class.java)
        }
    }

    fun reserveRoomForError(roomReservationRequest: RoomReservationRequest, expectedStatus: HttpStatus): ErrorResponse {
        val body = assertBodyMatchesSchema(roomReservationRequest, Guest.Reservations.ROOM_RESERVATION_REQUEST)

        return Given {
            body(body)
        } When {
            post("/guest/reservations")
        } Then {
            statusCode(expectedStatus.value())
            body(matchesJsonSchemaInClasspath(Shared.ERROR_RESPONSE))
        } Extract {
            `as`(ErrorResponse::class.java)
        }
    }

    fun reserveRoomForError(requestBody: String): Response {

        return Given {
            body(requestBody)
            filter(ResponseLoggingFilter())
        } When {
            post("/guest/reservations")
        }
    }

    fun getReservation(reservationId: Int): Reservation {
        return Given {
            pathParam("reservationId", reservationId)
        } When {
            get("/guest/reservations/{reservationId}")
        } Then {
            statusCode(HttpStatus.OK.value())
            body(matchesJsonSchemaInClasspath(Guest.Reservations.RESERVATION_RESPONSE))
        } Extract {
            `as`(Reservation::class.java)
        }
    }

}
