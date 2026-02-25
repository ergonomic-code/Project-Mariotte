package pro.azhidkov.mariotte.infra

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.testcontainers.postgresql.PostgreSQLContainer

/**
 * Ленивый докер-контейнер с PostgreSQL для тестов.
 * Использование RAM-диска (`withTmpFs` и `withEnv`) и хранению контейнера в статической переменной
 * обеспечивает медианное время выполнения теста на моём ноутбуке (Huawei MateBook 14s, i7-11370H, 16gm RAM, SSD) порядка 15мс.
 * А переиспользование контейнера (`withReuse` и `withInitScript`) обеспечивает среднне время запуска одного теста
 * (по логам) порядка 1.8 секунды.
 */
val pgContainer: PostgreSQLContainer by lazy {
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

val pgDataSource: HikariDataSource by lazy {
    HikariDataSource().apply {
        jdbcUrl = pgContainer.jdbcUrl
        username = pgContainer.username
        password = pgContainer.password

        // настройки для минимизации времени инициализации пула
        minimumIdle = 0
        maximumPoolSize = 2
        leakDetectionThreshold = 0
    }
}

/**
 * "Занос" в контекст "самодельного" DataSource-а-синглтона позволяет переиспользовать его
 * в разных контекстах и, тем самым, минимизировать время запуска второго и далее контекста.
 */
@TestConfiguration
class DbTestConfig {

    @Bean
    fun dataSource(): HikariDataSource =
        pgDataSource
}
