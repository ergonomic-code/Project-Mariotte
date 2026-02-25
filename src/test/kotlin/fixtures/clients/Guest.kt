package pro.azhidkov.mariotte.fixtures.clients

import org.springframework.test.web.servlet.client.RestTestClient
import pro.azhidkov.mariotte.fixtures.clients.apis.ReservationsHttpApi
import pro.azhidkov.mariotte.infra.objectMapper

/**
 * Сборник API фич приложения гостя.
 */
class Guest(client: RestTestClient) {

    val reservations: ReservationsHttpApi = ReservationsHttpApi(client, objectMapper)

    companion object {
        fun loginAsTheGuest(client: RestTestClient): Guest {
            return Guest(client)
        }
    }

}
