package pro.azhidkov.mariotte.infra.spring

import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.servlet.client.MockMvcWebTestClient
import org.springframework.web.context.WebApplicationContext
import pro.azhidkov.mariotte.HotelsApp
import pro.azhidkov.mariotte.backgrounds.Backgrounds
import pro.azhidkov.mariotte.fixtures.resetRandom
import pro.azhidkov.mariotte.infra.TestContainerDbContextInitializer


/**
 * Базовый класс для интеграционных тестов (1 шт. в этом проекте).
 * Берёт на себя всю работу по сетапу инфраструктуры.
 *
 * Метки в коде:
 * 1. Последние четыре года я был активным противником моков и фейков, в том числе MockMvc.
 *    Однако, с одной стороны, отказ от запуска реального Томкэта позволяет сэкономить секунду на старте одного теста
 *    и ещё какую-то копеечку на каждом вызове при запуске тестов.
 *
 *    А, с другой стороны, если что, вернуться к работе по HTTP можно 3 простыми шагами:
 *    1. поменять `webEnvironment` на `SpringBootTest.WebEnvironment.RANDOM_PORT`
 *    2. Вернуть `@LocalRandomPort val port`
 *    3. Поменять создание клиента на:
 *    ```
 *       WebTestClient.bindToServer()
 *         .baseUrl("http://localhost:$port")
 *         .defaultHeader("Content-Type", "application/json")
 *         .build()
 *    ```
 *
 *    Кроме того, теоретически можно отнаследоваться от `SpringBootTestContextBootstrapper` и добавить возможность
 *    выбирать `webEnvironment` через JVM Properties и на CI гонять тесты с HTTP, а локально - с MockMvc.
 */
@ActiveProfiles("test")
@Sql("classpath:db/reset-data.sql")
@ContextConfiguration(
    initializers = [TestContainerDbContextInitializer::class],
)
@SpringBootTest(
    classes = [HotelsApp::class, Backgrounds::class],
    webEnvironment = SpringBootTest.WebEnvironment.MOCK // 1
)
class MariotteBaseIntegrationTest {

    @Autowired
    private lateinit var webAppContext: WebApplicationContext

    protected lateinit var client: WebTestClient

    @BeforeEach
    fun setUp() {
        client = MockMvcWebTestClient
            .bindToApplicationContext(webAppContext)
            .configureClient()
            .defaultHeader("Content-Type", "application/json")
            .build()

        resetRandom()
    }

}