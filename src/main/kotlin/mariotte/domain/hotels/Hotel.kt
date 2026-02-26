package pro.azhidkov.mariotte.domain.hotels

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.data.relational.core.mapping.Table
import pro.azhidkov.platform.java.util.Uuids
import java.time.Instant
import java.util.*

typealias HotelRef = AggregateReference<Hotel, UUID>

/**
 * Сущность представляющая отель в системе.
 * Запись ресурса "Отели".
 *
 * Фактически это корень агрегата, включающего так же и номера.
 * Однако, т.к. в Spring Data JDBC агрегаты загружаются энергично (eager), а номеров в отеле может быть сотни,
 * то эта сущность вынесена в отдельный физический агрегат.
 * см. [Эргономичная ER-модель](https://azhidkov.pro/microposts/23/11/immutable-relation-data-model-v2/)
 */
@Table("hotels")
data class Hotel(
    @Id
    val id: UUID = Uuids.v7(),
    @CreatedDate
    val createdAt: Instant = Instant.now(),
    @LastModifiedDate
    val modifiedAt: Instant? = null,
    @Version
    val version: Int = 0
) {

    fun ref(): HotelRef =
        AggregateReference.to(id)

    companion object {
        fun ref(hotelId: UUID): HotelRef =
            AggregateReference.to(hotelId)
    }

}
