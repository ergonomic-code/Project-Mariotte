package pro.azhidkov.mariotte.backgrounds

import org.springframework.context.annotation.ComponentScan
import org.springframework.stereotype.Component


@ComponentScan
@Component
class Backgrounds(
    val hotelBackgrounds: HotelsBackgrounds,
)