package app.services.provider.component

import app.components.system.base.Native
import core.components.base.Component
import core.components.base.filterAvailableComponents
import core.entities.Entity
import core.utils.isSubclass
import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

object ComponentProvider {
    private val components = getComponents()

    private fun getComponents(): Components {
        val components = Reflections(
            ConfigurationBuilder().forPackages(
                "components"
            )
        ).getSubTypesOf(Component::class.java)
            .asSequence()
            .filter { !it.kotlin.isAbstract && kotlin.runCatching { it.kotlin.createInstance() }.isSuccess }
            .map { it.kotlin }.filterNot { it.isSubclass(Native::class) }.toList()
        val userComponents = mutableListOf<KClass<Component>>()
        val systemComponents = components.asSequence()
            .filter { it.qualifiedName.toString().startsWith("components.") }
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