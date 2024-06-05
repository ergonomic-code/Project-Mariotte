package pro.azhidkov.platform.spring.jdbc

import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.time.LocalDate

class PairRowMapper : RowMapper<Pair<LocalDate, Int>> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Pair<LocalDate, Int> {
        return rs.getObject(1, LocalDate::class.java) to rs.getInt(2)
    }
}