package pro.azhidkov.mariotte.cases.apps.platform.annotations

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import pro.azhidkov.mariotte.HotelsApp
import pro.azhidkov.mariotte.cases.apps.platform.config.HttpClientFactoryTestConf
import pro.azhidkov.mariotte.cases.platform.annotations.MariotteDbTest
import pro.azhidkov.mariotte.fixtures.presets.FixturePresetsConf

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
