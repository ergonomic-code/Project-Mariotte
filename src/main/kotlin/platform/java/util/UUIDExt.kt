package pro.azhidkov.platform.java.util

import java.util.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

object Uuids {

    @OptIn(ExperimentalUuidApi::class)
    fun v7(): UUID =
        Uuid.generateV7().toJavaUuid()

}
