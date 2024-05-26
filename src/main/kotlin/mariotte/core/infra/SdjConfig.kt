package pro.azhidkov.mariotte.core.infra

import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration
import pro.azhidkov.platform.spring.data.PGIntervalToPeriodConverter
import pro.azhidkov.platform.spring.data.PeriodToPGIntervalConverter


@Configuration
class SdjConfig : AbstractJdbcConfiguration() {

    override fun userConverters(): List<*> {
        return listOf(
            PeriodToPGIntervalConverter(),
            PGIntervalToPeriodConverter()
        )
    }

}