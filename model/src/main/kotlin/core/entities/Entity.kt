package core.entities

import core.scene.Scene

abstract class Entity(private val components: ComponentHolder = MapComponentHolder()) : ComponentHolder by components {
    var scene: Scene? = null
    private set
}