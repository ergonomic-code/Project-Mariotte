package pro.azhidkov.mariotte.domain.rooms

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import pro.azhidkov.mariotte.domain.hotels.HotelRef
import pro.azhidkov.platform.java.util.Uuids
import java.time.Instant
import java.util.*


/**
 * Сущность, представляющая номер в отеле.
 *
 * Фактически это слабая сущность (часть агрегата) сущности "Отель".
 * Однако в Spring Data JDBC агрегаты загружаются eagerly, а номеров в отеле может быть сотни,
 * поэтому эта сущность вынесена в отдельный физический агрегат.
 * см. [Эргономичная ER-модель](https://azhidkov.pro/microposts/23/11/immutable-relation-data-model-v2/)
 *
 * Комментарии к меткам в коде:
 * 1. Все технические поля (идентификатор, даты создания/модификации, версия и т.д.) вынесены в конец конструктора
 *    и имеют значения по умолчанию, чтобы упростить создание новых сущностей.
 */
@Table("rooms")
data class Room(
    @Column("hotel_ref")
    val hotel: HotelRef,
    val roomType: RoomType,
    val roomNumber: Int,

    @Id // 1
    val id: UUID = Uuids.v7(),
    @CreatedDate
    val createdAt: Instant = Instant.now(),
    @LastModifiedDate
    val modifiedAt: Instant? = null,
    @Version
    val version: Int = 0
)
