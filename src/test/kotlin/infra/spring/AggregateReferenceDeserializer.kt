package pro.azhidkov.mariotte.infra.spring

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import org.springframework.data.jdbc.core.mapping.AggregateReference
import pro.azhidkov.platform.spring.sdj.refs.AggregateReferenceTarget
import pro.azhidkov.platform.spring.sdj.refs.Identifiable


class AggregateReferenceDeserializer : JsonDeserializer<AggregateReference<*, *>>(), ContextualDeserializer {

    override fun deserialize(parser: JsonParser, context: DeserializationContext): AggregateReference<*, *>? {
        val node = parser.codec.readTree<JsonNode>(parser)
        val propertyNames = node.properties().map { it.key }.toSet()
        val type = context.getAttribute("referenceTargetType") as? JavaType?
            ?: error("Reference target type not set in context")

        return if (propertyNames == setOf("id")) {
            AggregateReference.to<Any, Any>(node.get("id").numberValue())
        } else if (propertyNames.size > 1) {
            AggregateReferenceTarget<Identifiable<Any>, Any>(context.readTreeAsValue(node, type))
        } else {
            null
        }
    }

    override fun createContextual(ctx: DeserializationContext, property: BeanProperty): JsonDeserializer<*> {
        ctx.setAttribute("referenceTargetType", property.type.containedType(0))
        return this
    }

}