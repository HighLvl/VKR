package app.components.system.model

import app.components.system.base.Native
import core.components.base.AddToSnapshot
import core.components.base.Component
import core.components.base.TargetEntity
import core.components.model.SnapshotInfo
import core.entities.Environment

@TargetEntity(Environment::class)
class SnapshotInfo : Component(), SnapshotInfo, Native {
    @AddToSnapshot(1)
    override var modelTime: Double = 0.0
    internal set
    @AddToSnapshot(2)
    override var dt: Double = 0.0
    internal set
}