package pro.azhidkov.mariotte.fixtures.clients

import org.springframework.test.web.servlet.client.RestTestClient
import tools.jackson.databind.json.JsonMapper


/**
 * Фабрика HTTP-клиентов приложения.
 * Инкапсулирует конкретный механизм исполнения запросов (RestTestClient) и возвращает клиенты АПИ (по ролям),
 * представляющие АПИ приложения в терминах домена.
 *
 * Метки в коде:
 * 1. В реальном приложении хотя бы часть API скорее всего будет требовать аутентификации и в этом случае
 *    вместо поля, надо будет сделать функцию, получающую на вход аутентификационные данные.
 *    И тогда параметры конструктора `client` и `jsonMapper` надо будет сделать полями класса.
 */
class HttpClientFactory(
    client: RestTestClient, // 1
    jsonMapper: JsonMapper
) {

    val aGuest = Guest(client, jsonMapper) // 1

}
