package pro.azhidkov.mariotte.assertions

import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath
import org.hamcrest.MatcherAssert


fun ObjectMapper.serializeToValidJson(body: Any, schema: String): String {
    val bodyStr = this.writeValueAsString(body)
    MatcherAssert.assertThat(bodyStr, matchesJsonSchemaInClasspath(schema))
    return bodyStr
}
