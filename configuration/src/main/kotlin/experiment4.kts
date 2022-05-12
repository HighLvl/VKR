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

    optimization(targetScore = 26) {
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

        fun isFilled(id: Int): Boolean {
            val agent = getAgent(id)!!
            val trashVolume = agent.getPropValue<Double>("trashVolume")!!
            val capacity = agent.getPropValue<Double>("capacity")!!
            return trashVolume >= capacity
        }

        goals {
            lastInstant("3", 5) {
                isFilled(3)
            }
            lastInstant("10", 10) {
                isFilled(10)
            }
            lastInstant("12", 4) {
                isFilled(12)
            }
            lastInstant("14", 1) {
                isFilled(14)
            }
            lastInstant("25", 1) {
                isFilled(25)
            }
            lastInstant("21", 8) {
                isFilled(21)
            }
            lastInstant("35", 9) {
                isFilled(35)
            }
            lastInstant("31", 5) {
                isFilled(31)
            }
            lastInstant("40", 3) {
                isFilled(40)
            }
            lastInstant("44", 4) {
                isFilled(44)
            }
        }

        makeDecisionOn {
            condition("Every snapshot") { true }
        }

        stop { isTargetScoreAchieved, _, _ ->
            if (isTargetScoreAchieved) {
                Logger.log("Население недовольно тем, что микрорайон утопает в мусоре", Level.INFO)
            } else {
                Logger.log("Население все устраивает", Level.INFO)
            }
        }
    }
}


