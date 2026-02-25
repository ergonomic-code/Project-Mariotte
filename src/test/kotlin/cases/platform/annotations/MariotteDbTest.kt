package pro.azhidkov.mariotte.cases.platform.annotations

import org.springframework.context.annotation.Import
import org.springframework.test.context.jdbc.Sql
import pro.azhidkov.mariotte.infra.DbTestConfig

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@MariotteRuntimeTest
@Sql("classpath:db/reset-data.sql")
@Import(DbTestConfig::class)
annotation class MariotteDbTest
