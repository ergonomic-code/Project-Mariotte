package pro.azhidkov.mariotte.fixtures.clients.apis

import com.networknt.schema.Schema
import com.networknt.schema.SchemaLocation
import com.networknt.schema.SchemaRegistry
import com.networknt.schema.SpecificationVersion


val schemaRegistry: SchemaRegistry = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12)
val jsonSchemaFactory: SchemaRegistry = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12)

fun SchemaRegistry.loadSchema(location: String): Schema {
    return jsonSchemaFactory.getSchema(SchemaLocation.of("classpath:$location"))
}

object SharedJsonSchemas {

    private const val SHARED = "${JsonSchemas.ROOT}/shared"

    const val ERROR_RESPONSE = "$SHARED/error-response.json"
    val errorResponseSchema = schemaRegistry.loadSchema(ERROR_RESPONSE)

}
