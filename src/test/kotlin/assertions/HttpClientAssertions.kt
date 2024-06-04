package pro.azhidkov.mariotte.assertions

import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath
import org.hamcrest.MatcherAssert


interface HttpClientAssertions {

    val objectMapper: ObjectMapper

    fun assertBodyMatchesSchema(body: Any, schema: String): String {
        val bodyStr = objectMapper
            .writeValueAsString(body)
        MatcherAssert.assertThat(bodyStr, matchesJsonSchemaInClasspath(schema))
        return bodyStr
    }

}