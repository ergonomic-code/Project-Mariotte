package pro.azhidkov.mariotte.clients

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import org.springframework.data.jdbc.core.mapping.AggregateReference
import pro.azhidkov.mariotte.clients.apis.ReservationsApi
import pro.azhidkov.mariotte.infra.spring.AggregateReferenceDeserializer

private val objectMapper = jacksonMapperBuilder()
    .addModule(JavaTimeModule())
    .addModule(
        SimpleModule(
            "aggregate-reference-module",
            Version.unknownVersion(),
            mapOf(AggregateReference::class.java to AggregateReferenceDeserializer())
        )
    )
    .build()

class Guest {

    val reservations: ReservationsApi = ReservationsApi(objectMapper)

    companion object {
        fun loginAsTheGuest(): Guest {
            return Guest()
        }
    }

}
