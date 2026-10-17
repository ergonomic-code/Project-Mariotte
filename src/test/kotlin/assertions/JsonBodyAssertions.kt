package pro.azhidkov.mariotte.assertions

import com.networknt.schema.InputFormat
import com.networknt.schema.Schema
import io.kotest.assertions.fail


fun assertJsonMatchesSchema(bodyStr: String, schema: Schema) {
    val validationResult = schema.validate(bodyStr, InputFormat.JSON)
    if (validationResult.isNotEmpty()) {
        fail(
            "JSON does not match schema $schema:\n" +
                    validationResult.joinToString("\n") { "- ${it.message}" } +
                    "\njson:\n$bodyStr"
        )
    }
}
