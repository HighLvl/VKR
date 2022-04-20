package core.services

import core.services.control.AgentModelControl
import core.services.scene.Scene

object Services {
    lateinit var agentModelControl: AgentModelControl
    private set
    lateinit var scene: Scene
    private set
}