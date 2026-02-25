package pro.azhidkov.mariotte.cases.platform.annotations

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import pro.azhidkov.mariotte.cases.platform.extensions.ResetRandomExtension

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@ActiveProfiles("test")
@ExtendWith(ResetRandomExtension::class)
annotation class MariotteRuntimeTest
