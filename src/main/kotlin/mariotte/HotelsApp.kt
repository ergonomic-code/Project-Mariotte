package pro.azhidkov.mariotte

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Корневой конфигурационный класс Spring Boot приложения.
 */
@SpringBootApplication
class HotelsApp

/**
 * Точка входа в приложение.
 */
fun main(args: Array<String>) {
	runApplication<HotelsApp>(*args)
}
