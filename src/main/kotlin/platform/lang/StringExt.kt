package pro.azhidkov.platform.lang


/**
 * Стандартный паттерн Эргономичной структуры программ - утилиты над стандартными типами оформляются в виде
 * функций-расширения в файле <TargetType>Ext, сами эти файлы разбиваются по пакетам, в общих чертах повторяющих
 * местоположение целевых классов в своих проектах, а сами подпакеты, в свою очередь, помещаются в пакет platform
 * того уровня, который содержит всех клиентов утилит.
 */

/**
 * Функция перевода из CamelCase в kebab-case
 */
val String.inKebabCase: String
    get() {
        val pattern = "(?<=.)[A-Z]".toRegex()
        return this.replace(pattern, "-$0").lowercase()
    }
