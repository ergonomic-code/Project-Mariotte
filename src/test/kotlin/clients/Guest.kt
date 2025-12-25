package pro.azhidkov.mariotte.clients

import org.springframework.test.web.servlet.client.RestTestClient
import pro.azhidkov.mariotte.clients.apis.ReservationsApi
import pro.azhidkov.mariotte.infra.objectMapper

/**
 * Сборник API фич приложения гостя.
 */
class Guest(client: RestTestClient) {

    val reservations: ReservationsApi = ReservationsApi(client, objectMapper)

    companion object {
        fun loginAsTheGuest(client: RestTestClient): Guest {
            return Guest(client)
        }
    }

}
