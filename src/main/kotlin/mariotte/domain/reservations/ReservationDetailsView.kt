package pro.azhidkov.mariotte.domain.reservations

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import pro.azhidkov.mariotte.domain.hotels.HotelRef
import pro.azhidkov.mariotte.domain.rooms.RoomType
import java.time.LocalDate
import java.util.*

/**
 * Read-модель бронирования для выдачи наружу (через HTTP-порт).
 *
 * Технически это Spring Data JDBC projection: маппится на ту же таблицу `reservations`,
 * но используется как отдельный тип для read-запросов, чтобы не тянуть за собой доменную сущность целиком
 * и не смешивать обязанности write-модели и модели ответа.
 */
@Table("reservations")
data class ReservationDetailsView(
    @Id val id: UUID,
    val hotelRef: HotelRef,
    val roomType: RoomType,
    val email: String,
    val from: LocalDate,
    val period: ReservationPeriod
)
