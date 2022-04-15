import core.components.experiment.experimentTask
import core.services.*
import core.services.logger.Level
import core.services.logger.Logger

experimentTask {
    var numberOfDoodleBugs = 0.0
    modelLifecycle {
        onRun {
        }
        onUpdate {
            numberOfDoodleBugs = getAgents().count { it.agentType == "Doodlebug" }.toDouble()
        }
        onStop { }
    }

    optimization(targetScore = 2) {
        inputParams {
            param("a", 100.0, 0.0, 200.0, 1.0) {
                requestSetValue(1, "a", it.toInt())
            }
        }
        targetFunction {
            expectedValue {
                numberOfDoodleBugs
            }

//            lastInstant {
//                numberOfDoodleBugs
//            }
        }
//        targetFunction {
//            MutableValueHolder(0.0, 0.0).apply {
//                var count = 0
//                begin {
//                    count = 0
//                }
//                update {
//                    if (numberOfDoodleBugs > 5) count++
//                    instantValue = count.toDouble()
//                }
//                end {
//                    value = count.toDouble()
//                }
//            }
//        }

        goals {
            lastInstant("last: Number of Doodlebugs > 5", 1) {
                numberOfDoodleBugs > 5
            }

            allInstant("all: Number of Doodlebugs > 5", 1) {
                numberOfDoodleBugs > 5
            }

            //custom("") {}
        }
        makeDecisionOn {
            modelTimeSinceLastDecision(20.0)
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
            ///timeSinceStart(timeMillis = 10000)
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