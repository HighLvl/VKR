import components.optimization.UserDecision
import components.optimization.experiment.OptimizationExperiment
import components.variables.Variables
import core.components.configuration.Configuration
import core.components.experiment.MutableValueHolder
import core.components.experiment.experimentTask
import core.coroutines.Contexts
import core.entities.getComponent
import core.entities.setComponent
import core.services.Services
import core.services.control.ControlState
import core.services.getAgents
import core.services.requestSetValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val modelConfigurationPath =
    "C:\\Users\\chere\\IdeaProjects\\DSL_test\\configuration\\src\\main\\kotlin\\configuration.kts"

fun loadModelConfiguration() {
    Services.scene.environment.getComponent<Configuration>()!!.modelConfiguration = modelConfigurationPath
}

fun setupComponents() = with(Services.scene) {
    with(experimenter) {
        setComponent<Variables>().apply {
            showObservableVariables = true
        }
        setComponent<OptimizationExperiment>().apply {
            showGoals = true
            showInputArgs = true
        }
        setComponent<UserDecision>()
    }
}

fun connectToModel() {
    CoroutineScope(Contexts.app).launch {
        Services.agentModelControl.connect("localhost", 4444)
    }
}

fun setup() {
    loadModelConfiguration()
    setupComponents()
    connectToModel()
}

setup()

experimentTask {
    var numberOfDoodleBugs = 0.0
    var targetFunctionVH = MutableValueHolder(0.0)

    modelLifecycle {
        onRun { }
        onUpdate {
            numberOfDoodleBugs = getAgents().count { it.agentType == "Doodlebug" }.toDouble()
        }
        onStop { }
        onPause { }
        onResume { }
    }

    variables {
        observable("Number of Ants") {
            getAgents().count { it.agentType == "Ant" }.toDouble()
        }
        observable("Number of Doodlebugs") {
            getAgents().count { it.agentType == "Doodlebug" }.toDouble()
        }
        observable("Target Function Value") {
            targetFunctionVH.value
        }
        mutable("a") { requestSetValue(1, "a", it.toInt()) }
    }

    optimization(targetScore = 2) {
        inputParams {
            param("a", 100.0, 0.0, 200.0, 1.0) {
                requestSetValue(1, "a", it.toInt())
            }
            constraint { params ->
                params["a"]!! != 10.0
            }
        }
        targetFunction {
            start {
                targetFunctionVH.value = 0.0
            }
            targetFunctionVH = expectedValue {
                numberOfDoodleBugs
            }
            targetFunctionVH
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
            timeSinceStart(timeMillis = 20000)
        }
    }
}