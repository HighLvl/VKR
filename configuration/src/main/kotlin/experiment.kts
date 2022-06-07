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

val modelConfigurationPath = "C:\\Users\\chere\\IdeaProjects\\IDIOMA\\configuration\\src\\main\\kotlin\\configuration.kts"

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


class TraversalOrderGenerator {
    private var garbageCans = listOf<Agent>()
    private lateinit var parking: Agent

    private fun findPath(from: Agent, to: Agent, index: Int): List<Int> {
        fun neighbours(id: Int): List<Int> {
            if (getAgent(id) == null) println(id)
            return getAgent(id)!!.getPropValue("neighbours")!!
        }

        val visited = mutableSetOf<Int>()
        val queue = mutableSetOf<Int>()
        queue.add(from.id)
        val idToParentIdMap = mutableMapOf<Int, Int>()
        val paths = mutableListOf<List<Int>>()
        while (queue.isNotEmpty()) {
            val nextId = queue.firstOrNull {it == to.id} ?: queue.first()
            visited.add(nextId)
            queue.remove(nextId)
            if (nextId == to.id) {
                var rNextId = to.id
                val path = mutableListOf<Int>()
                while (true) {
                    path.add(rNextId)
                    val parentId = idToParentIdMap[rNextId]!!
                    if (parentId == from.id) break
                    rNextId = parentId
                }
                path.add(from.id)
                path.reverse()
                paths.add(path)
                visited.remove(nextId)
                continue
            }
            for (neighbour in neighbours(nextId)) {
                if (neighbour !in visited) {
                    idToParentIdMap[neighbour] = nextId
                    queue.add(neighbour)
                }
            }
        }

        return paths[index % paths.size]
    }

    //index >= 1
    fun generate(orders: List<Pair<Int, Int>>): List<Int> {
        if (garbageCans.isEmpty()) {
            parking = getAgents().first { it.agentType == "Parking" }
            garbageCans = getAgents().asSequence().filter { it.agentType == "GarbageCan" }.toMutableList()
        }
        val orderedGarbageCans = garbageCans.asSequence()
            .mapIndexed { index, value -> index to value }
            .associateBy { orders[it.first] }
            .toList()
            .asSequence()
            .sortedBy { it.first.first }
            .map {
                it.second.second to it.first.second
            }.toList()


        val path = listOf(parking.id) + findPath(
            parking,
            orderedGarbageCans[0].first,
            orderedGarbageCans[0].second
        ) + (0 until orderedGarbageCans.lastIndex).map {
            findPath(
                orderedGarbageCans[it].first,
                orderedGarbageCans[it + 1].first,
                orderedGarbageCans[it + 1].second
            )
        }.flatten()
        val repeatsRemovedPath = mutableListOf<Int>()
        repeatsRemovedPath.add(path[0])
        for(i in 1..path.lastIndex) {
            if (path[i] != path[i - 1]) {
                repeatsRemovedPath.add(path[i])
            }
        }
        return repeatsRemovedPath
    }
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
        var traversalOrderGenerator = TraversalOrderGenerator()
        start {
            initialized = false
        }
        update {
            if (!initialized) {
                requestSetValue(-1, "hourSec", 0.0003)
                requestSetValue(car!!.id, "workOnSchedule", true)
                requestSetValue(car!!.id, "capacity", 22.0)
                requestSetValue(car!!.id, "speed", 60)
                requestSetValue(car!!.id, "restTime", 168.0)

                traversalOrderGenerator = TraversalOrderGenerator()
            }
            initialized = true
        }

        fun mapDecisionToTraversalOrder(it: Map<String, Double>): List<Int> {
            val orders =(0 until 10).map { i ->
                val order = it["{$i}Order"]!!.toInt()
                val pathNumber = it["{$i}PathNumber"]!!.toInt()
                order to pathNumber
            }
            return traversalOrderGenerator.generate(orders)
        }

        inputParams {
            for (i in 0 until 10) {
                param("{$i}Order", 0.0, 9.0, 1.0)
                param("{$i}PathNumber", 0.0, 1000.0, 1.0)
            }

            makeDecision {
                val traversalOrder = mapDecisionToTraversalOrder(it)
                requestSetValue(car!!.id, "traversalOrder", traversalOrder)
            }
        }

        targetFunction {
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
                }
            }
        }

        makeDecisionOn {
            modelTimeSinceLastDecision(24.0 * 7 * 8)
        }

        stopOn {
            condition("initial") {
                if (count == 0){
                    if (modelTime >= 24.0 * 7 * 8) {
                        val consumedFuel = (car!!.getPropValue<Double>("consumedFuel")!! / modelTime) * 1000
                        Logger.log(consumedFuel.toString(), Level.INFO)
                        count++
                        return@condition true
                    }
                }
                false
            }

            //modelTime(168.0)
            // modelTime(200000.0)
//            timeSinceStart(timeMillis = 20000)
        }

        stop { isTargetScoreAchieved, bestDecision, _ ->
            if (bestDecision.isEmpty()) return@stop
            val traversalOrder = mapDecisionToTraversalOrder(bestDecision)
            Logger.log(traversalOrder.toString(), Level.INFO)
            println(traversalOrder.toString())
            requestSetValue(car!!.id, "traversalOrder", traversalOrder)
        }
    }
}
