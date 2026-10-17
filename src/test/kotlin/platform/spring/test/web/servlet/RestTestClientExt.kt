package pro.azhidkov.mariotte.platform.spring.test.web.servlet

import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter
import org.springframework.test.web.servlet.client.RestTestClient
import tools.jackson.databind.json.JsonMapper

fun <B : RestTestClient.Builder<B>> B.configureForApi(jsonMapper: JsonMapper): B =
    defaultHeader("Content-Type", "application/json")
        .requestInterceptor(RequestBodySchemaValidationInterceptor)
        .configureMessageConverters { converters ->
            converters.registerDefaults()
            converters.withJsonConverter(JacksonJsonHttpMessageConverter(jsonMapper))
        }
