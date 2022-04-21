package app.services.scene.factory.entities

import app.utils.getAccessibleFunction
import core.components.base.Component
import core.utils.isSubclass
import core.utils.runBlockCatching
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

@Suppress("UNCHECKED_CAST")
class MapComponentHolder : ComponentHolder {
    private val components = mutableMapOf<KClass<out Component>, Component>()

    override fun <C : Component> setComponent(type: KClass<out C>): C {
        val component = type.createInstance()
        components[type] = component
        runBlockCatching {
            onAttach.call(component)
        }

        return component
    }

    @Suppress("UNCHECKED_CAST")
    override fun <C : Any> getComponent(type: KClass<C>): C? {
        return components.entries.firstOrNull { it.key.isSubclass(type) }?.value as? C
    }

    @Suppress("UNCHECKED_CAST")
    override fun <C : Component> removeComponent(type: KClass<out C>): C? {
        return (components.remove(type) as C?)?.also {
            runBlockCatching { onDetach.call(it) }
        }
    }

    override fun getComponents(): List<Component> {
        return components.values.toList()
    }

    private companion object {
        private val onAttach = getAccessibleFunction<Component>("onAttach")
        private val onDetach = getAccessibleFunction<Component>("onDetach")
    }
}