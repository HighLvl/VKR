package core.entities

abstract class Entity(private val components: ComponentHolder = MapComponentHolder()) : ComponentHolder by components