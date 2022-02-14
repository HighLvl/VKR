package core.datatypes.base

import kotlin.reflect.KClass

interface Series<T : Any> : Iterable<T> {
    val last: T?
    var capacity: Int
    val valueType: KClass<*>

    operator fun get(index: Int): T?

    fun sub(len: Int): Series<T>
}

interface MutableSeries<T : Any> : Series<T> {
    fun append(value: T)
}