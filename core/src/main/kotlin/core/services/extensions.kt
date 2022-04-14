package core.services

import core.components.agent.AgentInterface
import core.components.agent.Props
import core.components.agent.request
import core.components.configuration.InputArgsComponent
import core.components.model.SnapshotInfo
import core.entities.Agent
import core.entities.getComponent

fun getAgent(agentId: Int) = Services.scene.agents[agentId]
fun getAgents(): Collection<Agent> = Services.scene.agents.values
fun getAgentsToIdMap() = Services.scene.agents

val Agent.props: Props
    get() = this.getComponent<AgentInterface>()!!.props

inline fun <reified T : Any> Agent.getPropValue(propName: String): T? {
    val propValue = props[propName]
    if (propValue !is T) return null
    return propValue
}

inline fun <reified T : Any> getPropValue(agentId: Int, propName: String): T? {
    return getAgent(agentId)?.getPropValue(propName)
}

inline fun <reified T : Any> Agent.request(name: String, args: List<Any>, noinline onResult: (Result<T>) -> Unit = {}) {
    getComponent<AgentInterface>()!!.request(name, args, onResult)
}

inline fun <reified T : Any> request(
    agentId: Int,
    name: String,
    args: List<Any>,
    noinline onResult: (Result<T>) -> Unit = {}
) {
    getAgent(agentId)?.getComponent<AgentInterface>()?.request(name, args, onResult)
}

fun requestSetValue(agentId: Int, varName: String, value: Any, onResult: (Result<Unit>) -> Unit = {}) {
    getAgent(agentId)?.getComponent<AgentInterface>()?.requestSetValue(varName, value, onResult)
}

fun putInputArg(name: String, value: Any) {
    val inputArgs = Services.scene.environment.getComponent<InputArgsComponent>()!!
    inputArgs.put(name, value)
}

fun <T: Any> getInputArg(name: String): T {
    val inputArgs = Services.scene.environment.getComponent<InputArgsComponent>()!!
    return inputArgs.get(name)
}

val modelTime: Double by Services.scene.environment.getComponent<SnapshotInfo>()!!::modelTime
val dt: Double by Services.scene.environment.getComponent<SnapshotInfo>()!!::dt

