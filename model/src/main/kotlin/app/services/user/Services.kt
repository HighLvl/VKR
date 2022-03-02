package app.services.user

import core.entities.Agent
import core.entities.Environment
import core.entities.Experimenter

object Service {
    lateinit var agentModelControl: AgentModelControl
    internal set
    lateinit var scene: Scene
    internal set
}

interface AgentModelControl {
    fun changeRequestPeriod(periodSec: Float)
    suspend fun runModel()
    suspend fun pauseModel()
    suspend fun resumeModel()
    suspend fun stopModel()
}

interface Scene {
    val experimenter: Experimenter
    val environment: Environment
    val agents: Map<Int, Agent>
}