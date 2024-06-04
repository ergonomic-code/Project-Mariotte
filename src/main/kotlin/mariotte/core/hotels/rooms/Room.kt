package pro.azhidkov.mariotte.core.hotels.rooms

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import pro.azhidkov.mariotte.core.hotels.root.HotelRef
import java.time.Instant


/**
 * Сущность представляющая номер в отеле.
 * * Слой в Функциональной архитектуре: чистое ядро
 * * Тип блока в структурном дизайне: н/а
 * * Слой в чистой архитектуре: сущности
 * * Тип блока в Эргономичной архитектуре: запись ресурса
 *
 * Фактически это слабая сущность (часть агрегата) сущности "Отель".
 * Однако, т.к. в Spring Data JDBC агрегаты загружаются энергично (eager), а номеров в отеле может быть сотни,
 * то эта сущность вынесена в отдельный физический агрегат.
 * см. [Эргономичная ER-модель](https://azhidkov.pro/microposts/23/11/immutable-relation-data-model-v2/)
 *
 * Комментарии к меткам в коде:
 * 1. все технические поля (идентификатор, даты создания/модификации, версию и т.д.) я выношу в конец списка аргументов
 *    конструктора и указываю для них значения по умолчанию, чтобы упростить код создания объектов новых сущнсотей.
 */
@Table("rooms")
data class Room(
    @Column("hotel_ref")
    val hotel: HotelRef,
    val roomType: RoomType,
    val roomNumber: Int,

    @Id // 1
    val id: Int = 0,
    @CreatedDate
    val createdAt: Instant = Instant.now(),
    @LastModifiedDate
    val modifiedAt: Instant? = null,
    @Version
    val version: Int = 0
)
