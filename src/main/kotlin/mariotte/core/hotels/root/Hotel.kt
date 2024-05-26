package pro.azhidkov.mariotte.core.hotels.root

import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.data.relational.core.mapping.Table

typealias HotelRef = AggregateReference<Hotel, Int>

@Table("hotels")
data class Hotel(
    @Id
    val id: Int = 0
) {

    fun ref(): HotelRef =
        AggregateReference.to(id)

    companion object {
        fun ref(hotelId: Int): HotelRef =
            AggregateReference.to(hotelId)
    }

}
