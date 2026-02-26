package pro.azhidkov.mariotte.infra

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.MountableFile

/**
 * Ленивый докер-контейнер с PostgreSQL для тестов.
 * `withTmpFs` + `PGDATA` переносят данные в tmpfs, а `withReuse(true)` уменьшает стоимость
 * повторных запусков контекста между тестами.
 * Скрипт `db/pg-initdb.d/not-durable.sh` отключает durability-настройки для ускорения локальных прогонов.
 */
val pgContainer: PostgreSQLContainer by lazy {
    PostgreSQLContainer("postgres:18.2")
        .withExposedPorts(5432)
        .withTmpFs(mapOf("/var" to "rw"))
        .withEnv("PGDATA", "/var/lib/postgresql/data-not-mounted")
        .withReuse(true)
        .withCopyFileToContainer(
            MountableFile.forClasspathResource("db/pg-initdb.d/not-durable.sh", 493),
            "/docker-entrypoint-initdb.d/10-not-durable.sh"
        )
        .withInitScript("db/mariotte-db-init.sql")
        .apply {
            start()
            // Сначала подключаемся к БД postgres, затем пересоздаём mariotte и работаем уже с ней.
            this.withDatabaseName("mariotte")
        }
}

val pgDataSource: HikariDataSource by lazy {
    HikariDataSource().apply {
        jdbcUrl = pgContainer.jdbcUrl
        username = pgContainer.username
        password = pgContainer.password

        // Настройки для минимизации времени инициализации пула в тестах.
        minimumIdle = 0
        maximumPoolSize = 2
        leakDetectionThreshold = 0
    }
}

/**
 * Регистрирует переиспользуемый singleton DataSource в Spring-контексте тестов.
 * Это сокращает время запуска второго и последующих контекстов.
 */
@TestConfiguration
class DbTestConfig {

    @Bean
    fun dataSource(): HikariDataSource =
        pgDataSource
}
