import core.components.experiment.experimentTask
import core.services.getAgents
import core.services.requestSetValue

experimentTask {
    setTargetScore(6.0)
    goal("Number of Doodlebugs < 5") {
        getAgents().count { it.agentType == "Doodlebug" }
            .takeIf { it < 5 }
            ?.let { 10.0 } ?: 0.0
    }
    constraint("Number of Doodlebugs < 5") {
        getAgents().count { it.agentType == "Doodlebug" } < 5
    }
    stopOn {
        condition("") {
            val agents = getAgents()
            agents.none { it.agentType == "Ant" } || agents.none { it.agentType == "Doodlebug" }
        }
        timeGreaterOrEqualsTo(2000.0)
    }

    variables {
        observable("Number of Ants") {
            getAgents().count { it.agentType == "Ant" }.toDouble()
        }
        observable("Number of Doodlebugs"){
            getAgents().count { it.agentType == "Doodlebug" }.toDouble()
        }
        mutable("a") { requestSetValue(1, "a", it.toInt()) }
    }
}