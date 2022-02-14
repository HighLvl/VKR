package core.entities

import core.entities.base.ComponentHolder
import core.entities.base.MapComponentHolder
import core.scene.Scene

abstract class Entity(private val components: ComponentHolder = MapComponentHolder()) : ComponentHolder by components {
    var scene: Scene? = null
    private set
}