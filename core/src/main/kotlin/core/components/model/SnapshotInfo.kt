package core.components.model

import core.components.base.Component

interface SnapshotInfo : Component {
    val modelTime: Double
    val dt: Double
}