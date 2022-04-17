package app.services.provider.component

import app.components.system.base.Native
import core.components.base.Component
import core.components.base.TargetEntity
import core.components.base.filterAvailableComponents
import core.entities.Entity
import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf

object ComponentProvider {
    private val components = getComponents()

    private fun getComponents(): Components {
        val components = Reflections(
            ConfigurationBuilder().forPackages(
                "app.components"
            )
        ).getSubTypesOf(Component::class.java)
            .asSequence()
            .filter { !it.kotlin.isAbstract && kotlin.runCatching { it.kotlin.createInstance() }.isSuccess }
            .map { it.kotlin }.filterNot { it.isSubclassOf(Native::class) }.toList()
        val userComponents = components.asSequence()
            .filter { it.qualifiedName.toString().startsWith("app.components.user.") }
            .sortedBy { it.qualifiedName }.toList()
        val systemComponents = components.asSequence()
            .filter { it.qualifiedName.toString().startsWith("app.components.system.") }
            .sortedBy { it.qualifiedName }.toList()
        return Components(userComponents, systemComponents)
    }

    fun getAvailableComponents(entity: Entity): Components {
        return Components(
            filterAvailableComponents(entity, components.user),
            filterAvailableComponents(entity, components.system)
        )
    }
}



data class Components(
    val user: List<KClass<out Component>>,
    val system: List<KClass<out Component>>
)