package core.entities

abstract class Entity(private val componentHolder: ComponentHolder) : ComponentHolder by componentHolder