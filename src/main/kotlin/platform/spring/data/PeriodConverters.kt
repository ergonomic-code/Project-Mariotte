package pro.azhidkov.platform.spring.data

import org.postgresql.util.PGInterval
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import pro.azhidkov.platform.postgresql.toPGInterval
import pro.azhidkov.platform.postgresql.toPeriod
import java.time.Period

@WritingConverter
class PeriodToPGIntervalConverter : Converter<Period, PGInterval> {

    override fun convert(source: Period): PGInterval {
        return source.toPGInterval()
    }

}

@ReadingConverter
class PGIntervalToPeriodConverter : Converter<PGInterval, Period> {

    override fun convert(source: PGInterval): Period {
        return source.toPeriod()
    }

}
