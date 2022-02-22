package core.entities

import core.components.SystemComponent
import core.components.Component
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

class MapComponentHolder : ComponentHolder {
    private val components = mutableMapOf<KClass<out Component>, Component>()

    override fun setComponent(component: Component) {
        components[component::class] = component
    }

    @Suppress("UNCHECKED_CAST")
    override fun <C : Component> getComponent(type: KClass<C>): C? {
        return components[type] as? C
    }

    @Suppress("UNCHECKED_CAST")
    override fun <C : Component> removeComponent(type: KClass<C>): C? {
        if (type.isSubclassOf(SystemComponent::class)) return null
        return components.remove(type) as C?
    }

    override fun getComponents(): List<Component> {
        return components.values.toList()
    }
}