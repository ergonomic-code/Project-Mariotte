package pro.azhidkov.mariotte.platform.spring.test.web.servlet

import org.springframework.test.web.servlet.client.RestTestClient
import org.springframework.test.web.servlet.client.expectBody
import pro.azhidkov.mariotte.assertions.assertJsonMatchesSchema

inline fun <reified T : Any> RestTestClient.ResponseSpec.getBodyMatchingSchema(schema: String): T {
    return this.expectBodyMatchesSchema(schema)
        .expectBody<T>()
        .returnResult()
        .responseBody as T
}

fun RestTestClient.ResponseSpec.expectBodyMatchesSchema(schema: String): RestTestClient.ResponseSpec {
    this.expectBody().consumeWith {
        val responseBody = String(it.responseBody!!)
        assertJsonMatchesSchema(responseBody, schema)
    }
    return this
}
