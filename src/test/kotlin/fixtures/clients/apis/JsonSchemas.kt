package pro.azhidkov.mariotte.fixtures.clients.apis

/**
 * Константы с путями к Json-схемам.
 * Пути разбиты на отдельные файлы, так как если их все собрать в одном файле, то он будет обладать только лишь
 * логической функциональной связанностью ([logical cohesion](https://en.wikipedia.org/wiki/Cohesion_(computer_science))).
 *
 * @see ReservationsSchemas
 * @see SharedJsonSchemas
 */
object JsonSchemas {
    const val ROOT = "json-schemas/app"
}
