package pro.azhidkov.mariotte.infra

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.MapPropertySource
import org.testcontainers.containers.PostgreSQLContainer

/**
 * Ленивый докер-контейнер с PostgreSQL для тестов.
 * Использование RAM-диска (`withTmpFs` и `withEnv`) и хранению контейнера в статической переменной
 * обеспечивает медианное время выполнения теста на моём ноутбуке (Huawei MateBook 14s, i7-11370H, 16gm RAM, SSD) порядка 15мс.
 * А переиспользование контейнера (`withReuse` и `withInitScript`) обеспечивает среднне время запуска одного теста
 * (по логам) порядка 1.8 секунды.
 */
val pgContainer: PostgreSQLContainer<*> by lazy {
    PostgreSQLContainer("postgres:16.3")
        .withExposedPorts(5432)
        .withTmpFs(mapOf("/var" to "rw"))
        .withEnv("PGDATA", "/var/lib/postgresql/data-not-mounted")
        .withReuse(true)
        .withInitScript("db/mariotte-db-init.sql")
        .apply {
            start()
            // Сначала подключаемся к бд postgres, пересоздаём бд mariotte для обнуления фикстуры и подключаемся к ней
            this.withDatabaseName("mariotte")
        }
}

/**
 * Для прокидывания параметров подключения к контейнеру (в первую очерерь - всега случайному бай дезигн порту)
 * я использую ContextInitializer, а не @DynamicPropertySource, как в большинстве примеров, потому что
 * эта аннотация ["пачкает" контекст](https://docs.spring.io/spring-framework/reference/testing/testcontext-framework/ctx-management/caching.html),
 * что приводит к тому, что на каждый тест-кейс контекст запускается заново и это существенно увеличивает время прогона
 * тестов.
 */
class TestContainerDbContextInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        applicationContext.environment.propertySources.addFirst(
            MapPropertySource(
                "Integration Postgres test properties",
                mapOf(
                    "spring.datasource.url" to pgContainer.jdbcUrl,
                    "spring.datasource.username" to pgContainer.username,
                    "spring.datasource.password" to pgContainer.password,
                )
            )
        )
    }

}