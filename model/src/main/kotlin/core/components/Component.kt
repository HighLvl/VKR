package core.components

import app.services.user.Scene
import core.entities.Entity

annotation class IgnoreInSnapshot

abstract class Component {
    @IgnoreInSnapshot
    var entity: Entity? = null
        private set

    @IgnoreInSnapshot
    val scene: Scene?
        get() = entity?.scene
}

