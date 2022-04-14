package app.components.model

import core.components.base.AddInSnapshot
import core.components.model.SnapshotInfo

class SnapshotInfo : SnapshotInfo() {
    @AddInSnapshot(1)
    override var modelTime: Double = 0.0
    internal set
    @AddInSnapshot(2)
    override var dt: Double = 0.0
    internal set

}