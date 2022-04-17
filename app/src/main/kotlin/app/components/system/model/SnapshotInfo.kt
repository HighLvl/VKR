package app.components.system.model

import app.components.system.base.Native
import core.components.base.AddInSnapshot
import core.components.base.TargetEntity
import core.components.model.SnapshotInfo
import core.entities.Environment

@TargetEntity(Environment::class)
class SnapshotInfo : SnapshotInfo, Native {
    @AddInSnapshot(1)
    override var modelTime: Double = 0.0
    internal set
    @AddInSnapshot(2)
    override var dt: Double = 0.0
    internal set
}