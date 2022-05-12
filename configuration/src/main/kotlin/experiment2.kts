import core.components.configuration.Configuration
import core.components.experiment.experimentTask
import core.coroutines.Contexts
import core.entities.Agent
import core.entities.getComponent
import core.services.*
import core.services.logger.Level
import core.services.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val modelConfigurationPath =
    "C:\\Users\\chere\\IdeaProjects\\DSL_test\\configuration\\src\\main\\kotlin\\configuration.kts"

fun loadModelConfiguration() {
    Services.scene.environment.getComponent<Configuration>()!!.modelConfiguration = modelConfigurationPath
}

fun connectToModel() {
    CoroutineScope(Contexts.app).launch {
        Services.agentModelControl.connect("localhost", 4444)
    }
}

fun setup() {
    loadModelConfiguration()
    connectToModel()
}

setup()

experimentTask {
    var count = 0
    var car: Agent? = null
    modelLifecycle {
        onRun {
            car = null
        }
        onUpdate {
            car = car ?: getAgents().first { it.agentType == "Car" }
        }
    }
    variables {
        observable("Work Time") {
            car!!.getPropValue("totalWorkTime")!!
        }
        observable("Consumed Fuel") {
            car!!.getPropValue("consumedFuel")!!
        }
    }

    optimization(targetScore = 1) {
        var initialized = false
        start {
            initialized = false
        }
        update {
            if (!initialized) {
                request<Unit>(-1, "Restart", emptyList())
                requestSetValue(-1, "hourSec", 0.0003)
                requestSetValue(car!!.id, "workOnSchedule", true)
                requestSetValue(car!!.id, "capacity", 22.0)
                requestSetValue(car!!.id, "speed", 60)
            }
            initialized = true
        }

        inputParams {
            param("restTime", 1.0, 24.0 * 14, 1.0)

            makeDecision {
                requestSetValue(car!!.id, "restTime", it["restTime"]!!)
                request<Unit>(-1, "Restart", emptyList())
            }
        }

        targetFunction {
            val numberOfFilledGarbageCans = expectedValue {
                val garbageCans = getAgents().filter { it.agentType == "GarbageCan" }
                garbageCans.count {
                    it.getPropValue<Double>("trashVolume")!! >= it.getPropValue<Double>("capacity")!!
                }.toDouble()
            }
            custom {
                end {
                    value = if (numberOfFilledGarbageCans.value > 0.0) 0.0 else car!!.getPropValue("restTime")!!
                }
            }
        }

        makeDecisionOn {
            modelTimeSinceLastDecision(24.0 * 7 * 4 * 12)
        }

        stop { isTargetScoreAchieved, bestDecision, _ ->
            if (bestDecision.isEmpty()) return@stop
            Logger.log("Максимальное время ожидания: ${bestDecision["restTime"]}", Level.INFO)
        }
    }
}
