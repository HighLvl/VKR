package core.services.scene

import core.entities.Agent
import core.entities.Environment
import core.entities.Experimenter

interface Scene {
    val experimenter: Experimenter
    val environment: Environment
    val agents: Map<Int, Agent>
}