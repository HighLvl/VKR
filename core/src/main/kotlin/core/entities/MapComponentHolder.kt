package core.entities

import core.components.base.Component
import core.utils.isSubclass
import core.utils.runBlockCatching
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class MapComponentHolder : ComponentHolder {
    private val components = mutableMapOf<KClass<out Component>, Component>()

    override fun <C : Component> setComponent(type: KClass<out C>): C {
        getComponent(type)?.let { return it }
        return type.createInstance().also {
            components[type] = it
            runBlockCatching {
                it.onAttach()
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <C : Component> getComponent(type: KClass<C>): C? {
        return components.entries.firstOrNull { it.key.isSubclass(type) }?.value as? C
    }

    @Suppress("UNCHECKED_CAST")
    override fun <C : Component> removeComponent(type: KClass<C>): C? {
        return (components.remove(type) as C?)?.also { runBlockCatching { it.onDetach() } }
    }

    override fun getComponents(): List<Component> {
        return components.values.toList()
    }
}