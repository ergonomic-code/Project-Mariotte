package pro.azhidkov.mariotte.infra.spring

import org.springframework.data.jdbc.core.mapping.AggregateReference
import tools.jackson.core.JsonParser
import tools.jackson.databind.BeanProperty
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.JsonNode
import tools.jackson.databind.ValueDeserializer
import java.util.*


/**
 * Обеспечивает десериализацию полей типа AggregateReference (сейчас есть только в [pro.azhidkov.mariotte.core.reservations.ReservationDetails.hotel]).
 */
class AggregateReferenceDeserializer : ValueDeserializer<AggregateReference<*, *>>() {

    override fun deserialize(parser: JsonParser, context: DeserializationContext): AggregateReference<*, *>? {
        val node = parser.readValueAsTree<JsonNode>()
        val propertyNames = node.properties().map { it.key }.toSet()

        return if (propertyNames == setOf("id")) {
            AggregateReference.to<Any, Any>(UUID.fromString(node.get("id").asString()))
        } else {
            null
        }
    }

    override fun createContextual(ctx: DeserializationContext, property: BeanProperty): ValueDeserializer<*> {
        return this
    }

}
