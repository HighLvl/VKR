package core.scene

import core.entities.Optimizer
import core.entities.Agent
import core.entities.Environment

interface Scene {
    val optimizer: Optimizer
    val environment: Environment
    val agents: Map<Int, Agent>
}