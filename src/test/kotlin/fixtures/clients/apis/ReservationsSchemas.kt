package pro.azhidkov.mariotte.fixtures.clients.apis

object ReservationsSchemas {

    private const val RESERVATIONS = "${JsonSchemas.ROOT}/reservations"

    const val ROOM_RESERVATION_REQUEST = "$RESERVATIONS/create-reservation.json"
    val roomReservationRequestSchema = schemaRegistry.loadSchema(ROOM_RESERVATION_REQUEST)

    const val ROOM_RESERVATION_RESPONSE = "$RESERVATIONS/reservation-success.json"
    val roomReservationResponseSchema = schemaRegistry.loadSchema(ROOM_RESERVATION_RESPONSE)

    const val RESERVATION_RESPONSE = "$RESERVATIONS/reservation.json"
    val reservationResponseSchema = schemaRegistry.loadSchema(RESERVATION_RESPONSE)

}
