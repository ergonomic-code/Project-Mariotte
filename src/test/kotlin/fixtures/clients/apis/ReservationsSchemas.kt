package pro.azhidkov.mariotte.fixtures.clients.apis

object ReservationsSchemas {
    private const val RESERVATIONS = "${JsonSchemas.ROOT}/reservations"
    const val ROOM_RESERVATION_REQUEST = "$RESERVATIONS/create-reservation.json"
    const val ROOM_RESERVATION_RESPONSE = "$RESERVATIONS/reservation-success.json"
    const val RESERVATION_RESPONSE = "$RESERVATIONS/reservation.json"
}
