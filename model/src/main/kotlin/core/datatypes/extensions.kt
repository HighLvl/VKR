package core.datatypes

import core.datatypes.base.MutableSeries
import core.datatypes.base.Series

fun <T : Any> mutableSeriesOf(initCapacity: Int = Int.MAX_VALUE, vararg values: T?): MutableSeries<T> {
    return ListSeries(initCapacity, values.toMutableList())
}

fun <T : Any> seriesOf(initCapacity: Int = Int.MAX_VALUE, vararg values: T?): Series<T> {
    return ListSeries(initCapacity, values.toMutableList())
}