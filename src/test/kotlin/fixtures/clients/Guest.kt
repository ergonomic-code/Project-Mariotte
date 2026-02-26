package pro.azhidkov.mariotte.fixtures.clients

import org.springframework.test.web.servlet.client.RestTestClient
import pro.azhidkov.mariotte.fixtures.clients.apis.ReservationsHttpApi
import tools.jackson.databind.json.JsonMapper

/**
 * Тестовый фасад API фич приложения гостя.
 * Создаётся через [HttpClientFactory.aGuest], чтобы инфраструктурные детали не утекали в тест-кейсы.
 */
class Guest(
    client: RestTestClient,
    mapper: JsonMapper
) {

    val reservations: ReservationsHttpApi = ReservationsHttpApi(client, mapper)

}
