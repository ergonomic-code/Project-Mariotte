package pro.azhidkov.mariotte.infra

import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration
import pro.azhidkov.platform.spring.sdj.PGIntervalToPeriodConverter
import pro.azhidkov.platform.spring.sdj.PeriodToPGIntervalConverter


@Configuration
class SdjConfig : AbstractJdbcConfiguration() {

    override fun userConverters(): List<*> {
        return listOf(
            PeriodToPGIntervalConverter(),
            PGIntervalToPeriodConverter()
        )
    }

}