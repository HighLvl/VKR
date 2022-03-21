import core.components.experiment.experimentTask
import core.services.getAgents
import core.services.requestSetValue

experimentTask {
    setTargetScore(6.0)
//    goal("Number of Doodlebugs < 5") {
//        getAgents().count { it.agentType == "Doodlebug" }
//            .takeIf { it < 5 }
//            ?.let { 10.0 } ?: 0.0
//    }
//    constraint("Number of Doodlebugs < 5") {
//        getAgents().count { it.agentType == "Doodlebug" } < 5
//    }
    stopOn {
//        condition {
//            val agents = getAgents().values
//            agents.none { it.agentType == "Ant" } || agents.none { it.agentType == "Doodlebug" }
//        }
        timeGreaterOrEqualsTo(2000.0)
    }
    observableVariables(
        "Number of Ants" to { getAgents().count { it.agentType == "Ant" }.toDouble() },
        "Number of Doodlebugs" to { getAgents().count { it.agentType == "Doodlebug" }.toDouble() }
//        "x" to { getPropValue<Int>(0, "x")!!.toFloat() },
//        "y" to { getPropValue<Int>(0, "y")!!.toFloat() }
    )
    mutableVariable("a") { requestSetValue(0, "a", it.toInt()) }
}