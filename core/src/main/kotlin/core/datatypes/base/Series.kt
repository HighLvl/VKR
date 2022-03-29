package core.datatypes.base


interface Series<T : Any> : Iterable<T?> {
    val last: T?
    var capacity: Int
    val size: Int

    operator fun get(index: Int): T?

    fun sub(len: Int): Series<T>
}

interface MutableSeries<T : Any> : Series<T> {
    fun append(value: T?)
    operator fun set(index: Int, element: T): T
    fun clear()
}