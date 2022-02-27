package core.datatypes

import core.datatypes.base.MutableSeries
import core.datatypes.base.Series
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.KClass

class ListSeries<T : Any>(initCapacity: Int = Int.MAX_VALUE,
                          private val list: MutableList<T?> = ArrayList(initCapacity)
) : MutableSeries<T> {
    override val last: T?
        get() = list.getOrNull(list.size - 1)

    override var capacity: Int = initCapacity
        set(value) {
            field = value
            repeat(list.size - value) {
                list.removeFirst()
            }
        }
    override val size: Int
        get() = list.size

    override operator fun get(index: Int): T? {
        return list.getOrNull(list.size - index - 1)
    }

    override fun append(value: T?) {
        if (capacity == 0) return
        if (list.size == capacity) {
            list.removeFirst()
        }
        list.add(value)
    }

    override fun sub(len: Int): Series<T> {
        return ListSeries(capacity, list.subList(list.size - len - 1, list.size - 1))
    }

    override fun iterator(): Iterator<T?> {
        return list.asReversed().iterator()
    }
}