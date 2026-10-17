package pro.azhidkov.mariotte.cases.app.platform.config

import org.springframework.beans.factory.getBean
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.test.web.servlet.client.RestTestClient
import org.springframework.web.context.WebApplicationContext
import pro.azhidkov.mariotte.fixtures.clients.ApiSessionsFactory
import pro.azhidkov.mariotte.platform.spring.data.AggregateReferenceDeserializer
import pro.azhidkov.mariotte.platform.spring.test.web.servlet.configureForApi
import tools.jackson.databind.json.JsonMapper
import tools.jackson.databind.module.SimpleModule

@TestConfiguration
class ApiSessionsFactoryTestConf {

    @Bean
    fun apiSessionsFactory(webAppContext: WebApplicationContext): ApiSessionsFactory {
        val jsonMapper = webAppContext.getBean<JsonMapper>()
        val restTestClient = RestTestClient
            .bindToApplicationContext(webAppContext)
            .configureForApi(jsonMapper)
            .build()

        return ApiSessionsFactory(client = restTestClient)
    }

    @Bean
    fun aggregateReferenceModule(): SimpleModule {
        return SimpleModule()
            .addDeserializer(AggregateReference::class.java, AggregateReferenceDeserializer())
    }

}
