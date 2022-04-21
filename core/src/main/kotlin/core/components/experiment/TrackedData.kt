package core.components.experiment

import core.utils.Observable
import core.utils.ValueObservable

interface TrackedData {
    val trackedDataSizeObservable: ValueObservable<Int>
    val clearTrackedDataObservable: Observable<Unit>
}