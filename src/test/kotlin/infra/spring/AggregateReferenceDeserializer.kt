package pro.azhidkov.mariotte.infra.spring

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import org.springframework.data.jdbc.core.mapping.AggregateReference


/**
 * Обеспечивает десериализацию полей типа AggregateReference (сейчас есть только в [pro.azhidkov.mariotte.core.reservations.ReservationDetails.hotel]).
 */
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