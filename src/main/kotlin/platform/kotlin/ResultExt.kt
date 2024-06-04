package pro.azhidkov.platform.kotlin


fun <V> Result<V>.unwrap(): Any? =
    if (this.isSuccess) this.getOrNull() else this.exceptionOrNull()