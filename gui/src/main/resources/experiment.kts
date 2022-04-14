import core.components.experiment.experimentTask
import core.services.*

experimentTask {
    var numberOfDoodleBugs = 0.0
    modelLifecycle {
        onRun { }
        onUpdate {
            numberOfDoodleBugs = getAgents().count { it.agentType == "Doodlebug" }.toDouble()
        }
        onStop { }
    }

    optimization(targetScore = 6.0) {
        inputParams {
            param("a", 100.0) {
                requestSetValue(1, "a", it.toInt())
            }
//            param("Ants Number", 50.0) {
//                requestSetValue(1, "a", it.toInt())
//            }
        }
        goals {
            expectedValue("EV: Number of Doodlebugs >= 8", 8.0) {
                numberOfDoodleBugs
            }

            lastInstant("last: Number of Doodlebugs >= 8", 8.0) {
                numberOfDoodleBugs
            }

            custom("custom", 5.0) {
                var count = 0
                begin {
                    count = 0
                }
                update {
                    if (numberOfDoodleBugs > 5) count++
                    instantValue = count.toDouble()
                }
                end {
                    value = count.toDouble()
                }
            }
        }
        constraints {
            lastInstant("last: Number of Doodlebugs > 5") {
                numberOfDoodleBugs > 5
            }

            allInstant("all: Number of Doodlebugs > 5") {
                numberOfDoodleBugs > 5
            }

            //custom("") {}
        }
        makeDecisionOn {
            timeSinceLastDecision(20.0)
        }
        stopOn {
            condition("Number of Doodlebugs < 5") {
                numberOfDoodleBugs < 5
            }
            condition("some condition") {
                val agents = getAgents()
                agents.none { it.agentType == "Ant" } || agents.none { it.agentType == "Doodlebug" }
            }
            modelTime(2000.0)
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