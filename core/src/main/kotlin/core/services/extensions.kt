package core.services

import core.components.agent.AgentInterface
import core.components.agent.Props
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

fun <T: Any> Agent.request(name: String, args: List<Any>) {
    getComponent<AgentInterface>()!!.request<T>(name, args)
}

fun <T: Any> request(agentId: Int, name: String, args: List<Any>) {
    getAgent(agentId)?.getComponent<AgentInterface>()?.request<Any>(name, args)
}

fun requestSetValue(agentId: Int, varName: String, value: Any) {
    getAgent(agentId)?.getComponent<AgentInterface>()?.requestSetValue(varName, value)
}

