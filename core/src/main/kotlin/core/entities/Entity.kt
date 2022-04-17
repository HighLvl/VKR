package core.entities

import core.components.base.Component
import core.components.base.TargetEntity
import core.components.base.filterAvailableComponents
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf

abstract class Entity(private val componentHolder: ComponentHolder) : ComponentHolder by componentHolder {
    override fun <C : Component> setComponent(type: KClass<out C>): C {
        checkSetPossibility(type)
        return componentHolder.setComponent(type)
    }

    private fun <C : Component> checkSetPossibility(type: KClass<out C>) {
        if (filterAvailableComponents(this, listOf(type)).isEmpty()) {
            val annotation = this::class.findAnnotation<TargetEntity>()!!
            throw Exception("Target entity type: (${annotation.entityClass}, ${annotation.components})")
        }
    }

    override fun <C : Component> removeComponent(type: KClass<C>): C? {
        val component = componentHolder.removeComponent(type)
        removeDependentComponents(component!!)
        return component
    }

    private fun removeDependentComponents(removedComponent: Component) {
        getComponents().forEach { component ->
            component::class.findAnnotation<TargetEntity>()?.let { annotation ->
                if (annotation.components.any { removedComponent::class.isSubclassOf(it) }) {
                    removeComponent(component::class)
                }
            }
        }
    }

    private fun getUnavailableComponents(): List<KClass<out Component>> {
        val components = componentHolder.getComponents().map { it::class }
        return components - filterAvailableComponents(this, components)
    }
}