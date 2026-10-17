package pro.azhidkov.mariotte.fixtures.clients

import org.springframework.test.web.servlet.client.RestTestClient

/**
 * Сессия гостя, создаваемая через [ApiSessionsFactory.aGuest].
 */
class Guest(
    override val restClient: RestTestClient,
) : ApiSession
