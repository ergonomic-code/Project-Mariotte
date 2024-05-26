package pro.azhidkov.mariotte.apps.platform.java.lang


val String.inKebabCase: String
    get() {
        val pattern = "(?<=.)[A-Z]".toRegex()
        return this.replace(pattern, "-$0").lowercase()
    }

