package app.components.system.configuration

import core.components.configuration.AgentInterface
import core.components.base.Component
import kotlinx.coroutines.flow.StateFlow

interface AgentInterfaces {
    val agentInterfaces: StateFlow<Map<String, AgentInterface>>
}