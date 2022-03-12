package core.components.configuration

import app.components.configuration.AgentInterface
import core.components.Component

interface AgentInterfaces : Component {
    val agentInterfaces: Map<String, AgentInterface>
}

interface GlobalArgsComponent : Component {
    val globalArgs: Map<String, Any>
}