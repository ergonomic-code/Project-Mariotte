package pro.azhidkov.mariotte

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Плейсхолдер для аннотации @SpringBootApplication
 */
@SpringBootApplication
class HotelsApp

/**
 * Метод запуска приложения
 */
fun main(args: Array<String>) {
	runApplication<HotelsApp>(*args)
}
