package core.components

import core.entities.Entity

annotation class IgnoreInSnapshot

abstract class Component {
    @IgnoreInSnapshot
    var entity: Entity? = null
        private set
}

