package pro.azhidkov.mariotte.fixtures.clients

import org.springframework.test.web.servlet.client.RestTestClient

interface ApiSession {

    val restClient: RestTestClient

}
