package app.services.provider.component

import app.components.agent.AgentInterface
import app.components.experiment.Experiment
import core.components.base.Component
import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class ComponentProvider {
    fun getComponents(): Components {
        val components = Reflections(
            ConfigurationBuilder().forPackages(
                "app.components",
                "user"
            )
        ).getSubTypesOf(Component::class.java)
            .asSequence()
            .filter { !it.kotlin.isAbstract && kotlin.runCatching { it.kotlin.createInstance() }.isSuccess }
            .map { it.kotlin }.filterNot { it in listOf(AgentInterface::class, Experiment::class) }.toList()
        val userComponents = components.asSequence()
            .filter { it.qualifiedName.toString().startsWith("user.") }
            .sortedBy { it.qualifiedName }.toList()
        val appComponents = components.asSequence()
            .filter { it.qualifiedName.toString().startsWith("app.") }
            .sortedBy { it.qualifiedName }.toList()
        return Components(userComponents, appComponents)
    }
}

data class Components(
    val user: List<KClass<out Component>>,
    val app: List<KClass<out Component>>
)