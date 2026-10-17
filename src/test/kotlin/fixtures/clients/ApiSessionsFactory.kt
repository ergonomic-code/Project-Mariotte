package pro.azhidkov.mariotte.fixtures.clients

import org.springframework.test.web.servlet.client.RestTestClient

/**
 * Фабрика сессий АПИ приложения.
 * Инкапсулирует конкретный механизм исполнения запросов (RestTestClient) и возвращает сессии по ролям,
 * представляющие методы АПИ приложения в терминах домена.
 *
 * Метки в коде:
 * 1. В реальном приложении хотя бы часть API скорее всего будет требовать аутентификации и в этом случае
 *    вместо поля, надо будет сделать функцию, получающую на вход аутентификационные данные.
 *    И тогда `client` надо будет сделать полем класса.
 */
class ApiSessionsFactory(
    client: RestTestClient,
) {

    val aGuest = Guest(restClient = client) // 1

}
