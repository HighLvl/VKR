package app.services.user

import app.services.model.control.AgentModelControl
import core.entities.Agent
import core.entities.Environment
import core.entities.Experimenter

object Service {
    lateinit var agentModelControl: AgentModelControl
    internal set
    lateinit var scene: Scene
    internal set
}

interface Scene {
    val experimenter: Experimenter
    val environment: Environment
    val agents: Map<Int, Agent>
}