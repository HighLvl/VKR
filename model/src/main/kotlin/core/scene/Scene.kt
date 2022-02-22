package core.scene

import core.entities.Experimenter
import core.entities.Agent
import core.entities.Environment

interface Scene {
    val experimenter: Experimenter
    val environment: Environment
    val agents: Map<Int, Agent>
}