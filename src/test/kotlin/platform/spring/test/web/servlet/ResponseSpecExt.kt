package pro.azhidkov.mariotte.platform.spring.test.web.servlet

import com.networknt.schema.Schema
import org.springframework.test.web.servlet.client.RestTestClient
import org.springframework.test.web.servlet.client.expectBody
import pro.azhidkov.mariotte.assertions.assertJsonMatchesSchema

inline fun <reified T : Any> RestTestClient.ResponseSpec.getBodyMatchingSchema(schema: Schema): T {
    return this.expectBodyMatchesSchema(schema)
        .expectBody<T>()
        .returnResult()
        .responseBody as T
}

fun RestTestClient.ResponseSpec.expectBodyMatchesSchema(schema: Schema): RestTestClient.ResponseSpec {
    this.expectBody().consumeWith {
        val responseBody = String(it.responseBody!!)
        assertJsonMatchesSchema(responseBody, schema)
    }
    return this
}
