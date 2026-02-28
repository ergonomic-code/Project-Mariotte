package pro.azhidkov.mariotte.cases.domain.platform.annotations

import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import pro.azhidkov.mariotte.cases.platform.annotations.MariotteDbTest
import pro.azhidkov.mariotte.domain.infra.SdjConfig

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@MariotteDbTest
@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(SdjConfig::class)
annotation class MariotteSdjTest
