package pro.azhidkov.mariotte.platform.spring.test.web.servlet

import com.networknt.schema.Schema
import io.kotest.assertions.withClue
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.test.web.servlet.client.RestTestClient
import pro.azhidkov.mariotte.assertions.assertJsonMatchesSchema

private const val REQUEST_BODY_SCHEMA_ATTRIBUTE = "mariotte.tests.requestBodySchema"

fun <S : RestTestClient.RequestHeadersSpec<*>> S.ensureBodyMatchesSchema(schema: Schema): S =
    apply { attribute(REQUEST_BODY_SCHEMA_ATTRIBUTE, schema) }

object RequestBodySchemaValidationInterceptor : ClientHttpRequestInterceptor {

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution,
    ): ClientHttpResponse {
        val schema = request.attributes[REQUEST_BODY_SCHEMA_ATTRIBUTE] as? Schema
        if (schema != null) {
            val charset = request.headers.contentType?.charset ?: Charsets.UTF_8
            withClue("${request.method} ${request.uri}") {
                assertJsonMatchesSchema(body.toString(charset), schema)
            }
        }
        return execution.execute(request, body)
    }

}
