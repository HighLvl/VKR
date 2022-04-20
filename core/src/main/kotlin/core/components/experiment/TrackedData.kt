package core.components.experiment

import core.components.base.Component
import core.utils.Observable
import core.utils.ValueObservable

interface TrackedData: Component {
    val trackedDataSizeObservable: ValueObservable<Int>
    val clearTrackedDataObservable: Observable<Unit>
}