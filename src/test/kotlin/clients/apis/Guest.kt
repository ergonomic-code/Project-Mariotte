package pro.azhidkov.mariotte.clients.apis

object Guest {
    private const val GUEST_APP = "${JsonSchemas.ROOT}/guest"
    object Reservations {
        private const val GUEST_RESERVATIONS = "$GUEST_APP/reservations"
        const val ROOM_RESERVATION_REQUEST = "$GUEST_RESERVATIONS/create-reservation.json"
        const val ROOM_RESERVATION_RESPONSE = "$GUEST_RESERVATIONS/reservation-success.json"
        const val RESERVATION_RESPONSE = "$GUEST_RESERVATIONS/reservation.json"
    }
}