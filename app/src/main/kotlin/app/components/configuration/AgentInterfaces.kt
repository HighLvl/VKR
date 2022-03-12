package app.components.configuration

import core.components.configuration.AgentInterface
import core.components.base.Component

interface AgentInterfaces : Component {
    val agentInterfaces: Map<String, AgentInterface>
}