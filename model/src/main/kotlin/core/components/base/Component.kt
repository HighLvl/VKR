package core.components.base

import core.components.changePropertyValue
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

    open fun getSnapshot(): ComponentSnapshot {
        return ComponentConverter.convertToComponentSnapshot(this)
    }

    open fun loadSnapshot(snapshot: ComponentSnapshot) {
        snapshot.mutableProps.forEach {
            changePropertyValue(it.name, it.value)
        }
    }
}

