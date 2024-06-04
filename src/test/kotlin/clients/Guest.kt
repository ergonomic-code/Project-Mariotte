package pro.azhidkov.mariotte.clients

import pro.azhidkov.mariotte.clients.apis.ReservationsApi
import pro.azhidkov.mariotte.infra.objectMapper

class Guest {

    val reservations: ReservationsApi = ReservationsApi(objectMapper)

    companion object {
        fun loginAsTheGuest(): Guest {
            return Guest()
        }
    }

}
