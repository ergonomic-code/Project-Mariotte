package pro.azhidkov.mariotte.cases.app.platform.config

import org.springframework.beans.factory.getBean
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter
import org.springframework.test.web.servlet.client.RestTestClient
import org.springframework.web.context.WebApplicationContext
import pro.azhidkov.mariotte.fixtures.clients.HttpClientFactory
import pro.azhidkov.mariotte.platform.spring.data.AggregateReferenceDeserializer
import tools.jackson.databind.json.JsonMapper
import tools.jackson.databind.module.SimpleModule

@TestConfiguration
class HttpClientFactoryTestConf {

    @Bean
    fun httpClientFactory(webAppContext: WebApplicationContext): HttpClientFactory {
        val jsonMapper = webAppContext.getBean<JsonMapper>()
        val restTestClient = RestTestClient
            .bindToApplicationContext(webAppContext)
            .configureMessageConverters<RestTestClient.WebAppContextSetupBuilder> {
                it.registerDefaults()
                it.withJsonConverter(JacksonJsonHttpMessageConverter(jsonMapper))
            }
            .defaultHeader<RestTestClient.WebAppContextSetupBuilder>("Content-Type", "application/json")
            .build()

        return HttpClientFactory(restTestClient, jsonMapper)
    }

    @Bean
    fun aggregateReferenceModule(): SimpleModule {
        return SimpleModule()
            .addDeserializer(AggregateReference::class.java, AggregateReferenceDeserializer())
    }

}
