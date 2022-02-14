package core.datatypes

import core.datatypes.base.MutableSeries
import core.datatypes.base.Series

inline fun <reified T : Any> mutableSeriesOf(initCapacity: Int, vararg values: T): MutableSeries<T> {
    return ListSeries(initCapacity, values.toMutableList(), T::class)
}

inline fun <reified T : Any> seriesOf(initCapacity: Int, vararg values: T): Series<T> {
    return ListSeries(initCapacity, values.toMutableList(), T::class)
}