package pro.azhidkov.mariotte.fixtures.clients

import org.springframework.test.web.servlet.client.RestTestClient
import tools.jackson.databind.json.JsonMapper


/**
 * Фабрика HTTP-клиентов приложения.
 * Инкапсулирует конкретный механизм исполнения запросов (RestTestClient) и возвращает клиенты АПИ (по ролям),
 * представляющее АПИ приложения в терминах домена.
 *
 * Метки в коде
 * 1. В реальном API
 */
class HttpClientFactory(
    client: RestTestClient, // 1
    jsonMapper: JsonMapper
) {

    val aGuest = Guest(client, jsonMapper) // 1

}
