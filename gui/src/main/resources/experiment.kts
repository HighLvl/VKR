import app.components.experiment.experimentTask
import app.services.user.getAgents
import app.services.user.requestSetValue

experimentTask {
    setTargetScore(6)
    goal(10, "Number of Doodlebugs < 5") {
        getAgents().count { it.agentType == "Doodlebug" } < 5
    }
    constraint("Number of Doodlebugs < 5") {
        getAgents().count { it.agentType == "Doodlebug" } < 5
    }
    stopOn {
//        condition {
//            val agents = getAgents().values
//            agents.none { it.agentType == "Ant" } || agents.none { it.agentType == "Doodlebug" }
//        }
        timeGreaterOrEqualsTo(50f)
    }
    observableVariables(
        "Number of Ants" to { getAgents().count { it.agentType == "Ant" }.toFloat() },
        "Number of Doodlebugs" to { getAgents().count { it.agentType == "Doodlebug" }.toFloat() }
//        "x" to { getPropValue<Int>(0, "x")!!.toFloat() },
//        "y" to { getPropValue<Int>(0, "y")!!.toFloat() }
    )
    mutableVariable("a") { requestSetValue(0, "a", it.toInt()) }
}