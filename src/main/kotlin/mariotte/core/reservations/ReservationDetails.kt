package pro.azhidkov.mariotte.core.reservations

import pro.azhidkov.mariotte.core.hotels.rooms.RoomType
import pro.azhidkov.mariotte.core.hotels.root.HotelRef
import java.time.LocalDate

/**
 * Метки в коде:
 * 1. Ох... См. [раз](https://github.com/spring-projects/spring-data-relational/blob/abd0c85629756d34b98ca13b2a3eff341b832d25/spring-data-jdbc/src/main/java/org/springframework/data/jdbc/repository/query/JdbcQueryCreator.java#L249), [два](https://kotlinlang.org/docs/inline-classes.html#mangling), [три](https://youtrack.jetbrains.com/issue/KT-31420/Support-JvmName-on-interface-or-provide-other-interface-evolution-mechanism#focus=Comments-27-4211763.0-0),
 * [четыре](https://github.com/spring-projects/spring-framework/blob/f31113e325fa919d1fa18b409111411519daf4c5/spring-beans/src/main/java/org/springframework/beans/PropertyDescriptorUtils.java#L67).
 * Если вкратце - из-за того, что Kotlin манглирует имена методов, которые возвращают value classes в SQL-запрос не включалась колонка period и в итоге спринг не мог создать прокси из-за того,
 * что не мог найти period.
 * Я люблю Spring, Kotlin, Дебаггер, Идею и вообще весь современный технологический стэк.
 */
interface ReservationDetails {
    val hotel: HotelRef
    val roomType: RoomType
    val email: String
    val from: LocalDate

    @Suppress("INAPPLICABLE_JVM_NAME") // 1
    @get:JvmName("getPeriod")
    val period: ReservationPeriod
}
