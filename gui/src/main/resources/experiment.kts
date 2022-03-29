import core.components.experiment.experimentTask
import core.services.getAgents
import core.services.putInputArg
import core.services.requestSetValue

experimentTask {
    optimization(targetScore = 6.0) {
        inputParams {
            param("Doodlebugs Number", 100.0) {
                putInputArg("doodlebugs", it.toInt())
            }
            param("Ants Number", 50.0) {
                putInputArg("ants", it.toInt())
            }
        }
        goals {
            goal("Number of Doodlebugs > 8", 10.0) {
                getAgents().count { it.agentType == "Doodlebug" }.toDouble()
            }
        }
        constraints {
            constraint("Number of Doodlebugs > 5") {
                getAgents().count { it.agentType == "Doodlebug" } > 5
            }
        }
        stopOn {
            condition("Number of Doodlebugs < 5") {
                getAgents().count { it.agentType == "Doodlebug" } < 5
            }
            condition("some condition") {
                val agents = getAgents()
                agents.none { it.agentType == "Ant" } || agents.none { it.agentType == "Doodlebug" }
            }
            timeGreaterOrEqualsTo(2000.0)
        }
    }

    variables {
        observable("Number of Ants") {
            getAgents().count { it.agentType == "Ant" }.toDouble()
        }
        observable("Number of Doodlebugs") {
            getAgents().count { it.agentType == "Doodlebug" }.toDouble()
        }
        mutable("a") { requestSetValue(1, "a", it.toInt()) }
    }
}