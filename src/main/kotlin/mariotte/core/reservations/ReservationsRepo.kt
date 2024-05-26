package pro.azhidkov.mariotte.core.reservations

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import pro.azhidkov.mariotte.core.hotels.rooms.RoomType
import pro.azhidkov.mariotte.core.hotels.root.HotelRef
import java.sql.ResultSet
import java.time.LocalDate


@Repository
interface ReservationsRepo : CrudRepository<Reservation, Int> {

    @Query(
        """
            SELECT d::date, count(*)
            FROM generate_series(:from::date, :to::date, '1 days') AS d(date) 
            JOIN reservations r ON d.date BETWEEN r.from AND r.to
            WHERE
              hotel_ref = :hotel AND
              room_type = :roomType
            GROUP BY d
            ORDER BY d
    """,
        rowMapperClass = PairRowMapper::class
    )
    fun getReservationCountByDates(
        hotel: Int,
        roomType: RoomType,
        from: LocalDate,
        to: LocalDate
    ): Iterable<Pair<LocalDate, Int>>

}

fun ReservationsRepo.getActualReservations(
    hotel: HotelRef,
    roomType: RoomType,
    from: LocalDate,
    to: LocalDate
) = this.getReservationCountByDates(hotel.id!!, roomType, from, to)
    .toMap()

private class PairRowMapper : RowMapper<Pair<LocalDate, Int>> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Pair<LocalDate, Int> {
        return rs.getObject(1, LocalDate::class.java) to rs.getInt(2)
    }
}