package pro.azhidkov.mariotte.assertions

import com.networknt.schema.*
import io.kotest.assertions.fail
import tools.jackson.databind.ObjectMapper


private val schemaRegistry: SchemaRegistry = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12)

fun assertJsonMatchesSchema(bodyStr: String, schemaPath: String) {
    val schema: Schema = schemaRegistry.getSchema(SchemaLocation.of("classpath:$schemaPath"))

    val validationResult = schema.validate(bodyStr, InputFormat.JSON)
    if (validationResult.isNotEmpty()) {
        fail(
            "JSON does not match schema $schemaPath:\n" +
                    validationResult.joinToString("\n") { "- ${it.message}" } +
                    "\njson:\n$bodyStr"
        )
    }
}

/**
 * Метод сериализации объекта в JSON и проверка результата на соответствие схеме.
 *
 * Он обладает последовательной функциональной связностью ([sequence cohesion](https://en.wikipedia.org/wiki/Cohesion_(computer_science)))
 * и это влечёт проблему - непонятно, куда его складывать в пакет assertions или в платформу, как расширение ObjectMapper.
 * Я решил положить его в assertions, так как основная функция этого метода заключается всё-таки в эффекте проверки,
 * а сериализация и возврат результата добавлены в этот метод просто для удобства.
 *
 * Вариант сделать этот метод процедурой и возвращать Unit не подходит, т.к. он допускает возможность того, что верификацию
 * на соответствие схеме будет проходить одна строка, а потом по сети уйдёт другая строка.
 *
 */
fun ObjectMapper.serializeToValidJson(body: Any, schema: String): String {
    val bodyStr = this.writeValueAsString(body)
    assertJsonMatchesSchema(bodyStr, schema)
    return bodyStr
}
