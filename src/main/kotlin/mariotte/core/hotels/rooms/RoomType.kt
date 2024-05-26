package pro.azhidkov.mariotte.core.hotels.rooms

import com.fasterxml.jackson.annotation.JsonValue


enum class RoomType(
    @JsonValue
    val id: Int
) {

    LUX(1),
    SEMI_LUX(2);

}