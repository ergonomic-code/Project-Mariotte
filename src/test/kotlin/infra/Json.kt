package pro.azhidkov.mariotte.infra

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import org.springframework.data.jdbc.core.mapping.AggregateReference
import pro.azhidkov.mariotte.infra.spring.AggregateReferenceDeserializer


val objectMapper: JsonMapper = jacksonMapperBuilder()
    .addModule(JavaTimeModule())
    .addModule(
        SimpleModule(
            "aggregate-reference-module",
            Version.unknownVersion(),
            mapOf(AggregateReference::class.java to AggregateReferenceDeserializer())
        )
    )
    .build()
