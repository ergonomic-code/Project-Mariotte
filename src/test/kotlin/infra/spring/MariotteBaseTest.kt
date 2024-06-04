package pro.azhidkov.mariotte.infra.spring

import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.config.ObjectMapperConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.http.ContentType
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.jdbc.Sql
import pro.azhidkov.mariotte.HotelsApp
import pro.azhidkov.mariotte.backgrounds.Backgrounds
import pro.azhidkov.mariotte.fixtures.resetRandom
import pro.azhidkov.mariotte.infra.TestContainerDbContextInitializer
import pro.azhidkov.mariotte.infra.objectMapper


@Sql("classpath:db/reset-data.sql")
@ContextConfiguration(initializers = [TestContainerDbContextInitializer::class])
@SpringBootTest(
    classes = [HotelsApp::class, Backgrounds::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class MariotteBaseTest {

    @LocalServerPort
    private var port: Int = 0

    @BeforeEach
    fun setUp() {
        resetRandom()
        val config = RestAssuredConfig.config()
            .objectMapperConfig(ObjectMapperConfig().jackson2ObjectMapperFactory { _, _ ->
                objectMapper
            })

        RestAssured.requestSpecification = RequestSpecBuilder()
            .setBaseUri("http://localhost:$port")
            .setContentType(ContentType.JSON)
            .setRelaxedHTTPSValidation()
            .setConfig(config)
            .build()
    }

}