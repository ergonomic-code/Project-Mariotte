package pro.azhidkov.mariotte.platform.spring.test.web.servlet

import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import pro.azhidkov.mariotte.assertions.assertJsonMatchesSchema

inline fun <reified T : Any> WebTestClient.ResponseSpec.getBodyMatchingSchema(schema: String): T {
    return this.expectBodyMatchesSchema(schema)
        .expectBody<T>()
        .returnResult()
        .responseBody as T
}

fun WebTestClient.ResponseSpec.expectBodyMatchesSchema(schema: String): WebTestClient.ResponseSpec {
    this.expectBody().consumeWith {
        val responseBody = String(it.responseBody!!)
        assertJsonMatchesSchema(responseBody, schema)
    }
    return this
}
