package core.entities

import core.components.base.Component
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubclassOf

class MapComponentHolder : ComponentHolder {
    private val components = mutableMapOf<KClass<out Component>, Component>()

    override fun <C: Component> setComponent(type: KClass<out C>): C {
        return type.createInstance().also { components[type] = it }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <C : Component> getComponent(type: KClass<C>): C? {
        return components.entries.firstOrNull { it.key.isSubclassOf(type) }?.value as? C
    }

    @Suppress("UNCHECKED_CAST")
    override fun <C : Component> removeComponent(type: KClass<C>): C? {
        return components.remove(type) as C?
    }

    override fun getComponents(): List<Component> {
        return components.values.toList()
    }
}