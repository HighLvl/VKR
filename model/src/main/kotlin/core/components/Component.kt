package core.components

import core.entities.Entity
import core.scene.Scene

annotation class IgnoreInSnapshot

abstract class Component {
    @IgnoreInSnapshot
    var entity: Entity? = null
        private set

    @IgnoreInSnapshot
    val scene: Scene?
        get() = entity?.scene
}

