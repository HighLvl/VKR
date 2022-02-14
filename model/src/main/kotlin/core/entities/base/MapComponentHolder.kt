package core.entities.base

import core.components.base.Component
import kotlin.reflect.KClass

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
        return components.remove(type) as C?
    }
}