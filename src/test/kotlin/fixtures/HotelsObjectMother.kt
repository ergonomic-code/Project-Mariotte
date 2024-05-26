package pro.azhidkov.mariotte.fixtures

import pro.azhidkov.mariotte.core.hotels.root.Hotel


object HotelsObjectMother {

    // Вставляется в data.sql
    fun theHotel() =
        Hotel.ref(1)

    fun hotel(): Hotel =
        Hotel()

}