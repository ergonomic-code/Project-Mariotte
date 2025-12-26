package pro.azhidkov.mariotte.infra

import org.springframework.data.jdbc.core.mapping.AggregateReference
import pro.azhidkov.mariotte.infra.spring.AggregateReferenceDeserializer
import tools.jackson.core.Version
import tools.jackson.databind.json.JsonMapper
import tools.jackson.databind.module.SimpleModule
import tools.jackson.module.kotlin.jacksonMapperBuilder


val objectMapper: JsonMapper = jacksonMapperBuilder()
    .addModule(
        SimpleModule("aggregate-reference-module", Version.unknownVersion()).apply {
            addDeserializer(AggregateReference::class.java, AggregateReferenceDeserializer())
        }
    )
    .build()
