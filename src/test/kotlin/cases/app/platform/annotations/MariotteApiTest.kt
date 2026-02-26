package pro.azhidkov.mariotte.cases.app.platform.annotations

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import pro.azhidkov.mariotte.HotelsApp
import pro.azhidkov.mariotte.cases.app.platform.config.HttpClientFactoryTestConf
import pro.azhidkov.mariotte.cases.platform.annotations.MariotteDbTest
import pro.azhidkov.mariotte.fixtures.presets.FixturePresetsConf

/**
 * Составная аннотация для тестов API:
 * поднимает Spring-контекст приложения, подключает тестовую БД и HTTP-клиент.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@MariotteDbTest
@SpringBootTest(
    classes = [HotelsApp::class, FixturePresetsConf::class],
    webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@Import(HttpClientFactoryTestConf::class)
annotation class MariotteApiTest
