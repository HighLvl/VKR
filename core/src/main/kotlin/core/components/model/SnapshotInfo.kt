package core.components.model

import core.components.base.Component

abstract class SnapshotInfo: Component {
    abstract val modelTime: Double
    abstract val dt: Double
}