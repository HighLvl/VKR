package app.services.scene.factory.entities

import app.components.system.base.Native
import core.components.base.Component
import core.components.base.TargetEntity
import core.components.base.filterAvailableComponents
import core.entities.Entity
import core.utils.isSubclass
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

abstract class EntityImpl : Entity {
    private val componentHolder = MapComponentHolder()

    override fun <C : Component> setComponent(type: KClass<out C>): C {
        componentHolder.getComponent(type)?.let { return it }
        checkSetPossibility(type)
        return componentHolder.setComponent(type)
    }

    private fun <C : Component> checkSetPossibility(type: KClass<out C>) {
        if (filterAvailableComponents(this, listOf(type)).isEmpty()) {
            this::class.findAnnotation<TargetEntity>()?.let { annotation ->
                throw Exception("Target entity type: (${annotation.entityClass}, ${annotation.components})")
            } ?: throw Exception("Cannot set component")
        }
    }

    override fun <C : Component> removeComponent(type: KClass<C>): C? {
        if (type.isSubclass(Native::class)) return null
        val component = componentHolder.removeComponent(type)
        removeDependentComponents(component!!)
        return component
    }

    private fun removeDependentComponents(removedComponent: Component) {
        getComponents().forEach { component ->
            component::class.findAnnotation<TargetEntity>()?.let { annotation ->
                if (annotation.components.any { removedComponent::class.isSubclass(it) }) {
                    removeComponent(component::class)
                }
            }
        }
    }

    private fun getUnavailableComponents(): List<KClass<out Component>> {
        val components = componentHolder.getComponents().map { it::class }
        return components - filterAvailableComponents(this, components)
    }

    override fun <C : Component> getComponent(type: KClass<C>): C? {
        return componentHolder.getComponent(type)
    }

    override fun getComponents(): List<Component> {
        return componentHolder.getComponents()
    }
}