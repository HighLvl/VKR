package app.components.configuration

import core.components.configuration.AgentInterface
import core.components.base.Component
import kotlinx.coroutines.flow.StateFlow

interface AgentInterfaces : Component {
    val agentInterfaces: StateFlow<Map<String, AgentInterface>>
}