package core.services.scene

import core.components.base.Component
import core.entities.*
import kotlin.reflect.KClass

interface Scene {
    val experimenter: Experimenter
    val environment: Environment
    val agents: Map<Int, Agent>
    val agentPrototypes: Map<String, AgentPrototype>
    fun findEntityByComponent(component: Component): Entity?
    fun findAgentsThatHaving(vararg componentClass: KClass<*>): List<Agent>
}