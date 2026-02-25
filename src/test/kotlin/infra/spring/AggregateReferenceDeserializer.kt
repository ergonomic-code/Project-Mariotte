package pro.azhidkov.mariotte.infra.spring

import org.springframework.data.jdbc.core.mapping.AggregateReference
import tools.jackson.core.JsonParser
import tools.jackson.databind.BeanProperty
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.JavaType
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.exc.MismatchedInputException
import tools.jackson.databind.node.ObjectNode
import tools.jackson.databind.type.TypeFactory


/**
 * Обеспечивает десериализацию полей типа AggregateReference (сейчас есть только в [pro.azhidkov.mariotte.core.reservations.ReservationDetailsView.hotelRef]).
 */
class AggregateReferenceDeserializer(
    private val idType: JavaType = TypeFactory.unknownType()
) : ValueDeserializer<AggregateReference<*, *>>() {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): AggregateReference<*, *> {
        val node = ctxt.readTree(p)
        val idNode = if (node is ObjectNode && node.has("id")) node.get("id") else node
        val idValue: Any = ctxt.readTreeAsValue(idNode, idType)
            ?: throw MismatchedInputException.from(p, idType.rawClass, "ID deserialized to null")

        return AggregateReference.to<Any, Any>(idValue)
    }

    override fun createContextual(
        ctxt: DeserializationContext,
        property: BeanProperty?
    ): ValueDeserializer<*> {
        val refType = property?.type
            ?: ctxt.contextualType
            ?: return this

        val idType = refType.containedType(1)

        return AggregateReferenceDeserializer(idType)
    }

}