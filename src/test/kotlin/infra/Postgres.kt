package pro.azhidkov.mariotte.infra

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.MapPropertySource
import org.testcontainers.containers.PostgreSQLContainer

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