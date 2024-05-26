package pro.azhidkov.mariotte.core.hotels.root

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

typealias HotelRef = AggregateReference<Hotel, Int>

/**
 * Сущность представляющая отель в системе.
 * Запись ресурса "Отели".
 *
 * * Слой в Функциональной архитектуре: чистое ядро
 * * Тип блока в структурном дизайне: н/а
 * * Слой в чистой архитектуре: сущности
 * * Тип блока в Эргономичной архитектуре: запись ресурса
 *
 * Фактически это корень агрегата, включающего так же и номера.
 * Однако, т.к. в Spring Data JDBC агрегаты загружаются энергично (eager), а номеров в отеле может быть сотни,
 * то эта сущность вынесена в отдельный физический агрегат.
 * см. [Эргономичная ER-модель](https://azhidkov.pro/microposts/23/11/immutable-relation-data-model-v2/)
 */
@Table("hotels")
data class Hotel(
    @Id
    val id: Int = 0,
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
        fun ref(hotelId: Int): HotelRef =
            AggregateReference.to(hotelId)
    }

}
