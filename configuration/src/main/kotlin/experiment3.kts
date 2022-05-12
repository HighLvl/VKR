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
    var car: Agent? = null
    modelLifecycle {
        onRun {
            car = null
        }
        onUpdate {
            car = car ?: getAgents().first { it.agentType == "Car" }
        }
    }

    var workOnSchedule = 0.0
    var workOnScheduleCount = 0
    var cleverCanCount = 0
    var workOnScheduleFilledCans = 0.0
    var cleverFilledCans = 0.0
    var fuelComsumingSpeed = 0.0

    variables {
        observable("Work On Schedule") {
            workOnSchedule
        }
        observable("Fuel Consuming Speed * 1000") {
            -fuelComsumingSpeed
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
                requestSetValue(car!!.id, "restTime", 48.0)
            }
            initialized = true
        }

        inputParams {
            param("workOnSchedule", 0.0, 1.0, 1.0)

            makeDecision {
                requestSetValue(car!!.id, "workOnSchedule", it["workOnSchedule"]!! > 0.0)
                request<Unit>(-1, "Restart", emptyList())
            }
        }

        targetFunction {
            val numberOfFilledGarbageCans = expectedValue {
                val garbageCans = getAgents().filter { it.agentType == "GarbageCan" }
                garbageCans.count {
                    it.getPropValue<Double>("trashVolume")!! >= it.getPropValue<Double>("capacity")!!
                }.toDouble().coerceAtMost(1.0)
            }
            end {
                if (workOnSchedule == 1.0) {
                    workOnScheduleCount++
                    workOnScheduleFilledCans += numberOfFilledGarbageCans.value
                    workOnSchedule = if(car!!.getPropValue("workOnSchedule")!!) 1.0 else 0.0
                }
                else {
                    cleverCanCount++
                    cleverFilledCans += numberOfFilledGarbageCans.value
                    workOnSchedule = if(car!!.getPropValue("workOnSchedule")!!) 1.0 else 0.0
                }
            }
//            end {
//                Logger.log("Clever", Level.INFO)
//                Logger.log((cleverFilledCans / cleverCanCount).toString(), Level.INFO)
//                Logger.log("Work on schedule", Level.INFO)
//                Logger.log((workOnScheduleFilledCans / workOnScheduleCount).toString(), Level.INFO)
//            }
//            stop { _, _, _ ->
//                Logger.log((cleverFilledCans / cleverCanCount).toString(), Level.INFO)
//                Logger.log((workOnScheduleFilledCans / workOnScheduleCount).toString(), Level.INFO)
//            }

            custom {
                var prevConsumedFuel = 0.0
                var prevModelTime = 0.0
                begin {
                    prevConsumedFuel = car!!.getPropValue("consumedFuel")!!
                    prevModelTime = modelTime
                }
                end {
                    val consumedFuel = car!!.getPropValue<Double>("consumedFuel")!!
                    value = (prevConsumedFuel - consumedFuel) / (modelTime - prevModelTime) * 1000
                    fuelComsumingSpeed = value
                }
            }
        }

        makeDecisionOn {
            modelTimeSinceLastDecision(24.0 * 7 * 2 * 6)
        }

        stop { isTargetScoreAchieved, bestDecision, _ ->
            if (bestDecision.isEmpty()) return@stop
            if(bestDecision["workOnSchedule"] == 1.0) {
                Logger.log("Обход баков по расписанию оптимальней", Level.INFO)
            }
        }
    }
}