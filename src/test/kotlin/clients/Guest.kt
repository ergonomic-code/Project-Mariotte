package pro.azhidkov.mariotte.clients

import org.springframework.test.web.reactive.server.WebTestClient
import pro.azhidkov.mariotte.clients.apis.ReservationsApi
import pro.azhidkov.mariotte.infra.objectMapper

class Guest(client: WebTestClient) {

    val reservations: ReservationsApi = ReservationsApi(client, objectMapper)

    companion object {
        fun loginAsTheGuest(client: WebTestClient): Guest {
            return Guest(client)
        }
    }

}
