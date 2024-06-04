package pro.azhidkov.platform.spring.sdj

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import org.springframework.data.jdbc.core.mapping.AggregateReference


class AggregateReferenceDeserializer : JsonDeserializer<AggregateReference<*, *>>(), ContextualDeserializer {

    override fun deserialize(parser: JsonParser, context: DeserializationContext): AggregateReference<*, *>? {
        val node = parser.codec.readTree<JsonNode>(parser)
        val propertyNames = node.properties().map { it.key }.toSet()

        return if (propertyNames == setOf("id")) {
            AggregateReference.to<Any, Any>(node.get("id").numberValue())
        } else {
            null
        }
    }

    override fun createContextual(ctx: DeserializationContext, property: BeanProperty): JsonDeserializer<*> {
        return this
    }

}