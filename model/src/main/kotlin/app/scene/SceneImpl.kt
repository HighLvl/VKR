package app.scene

import core.entities.Agent
import core.entities.Entity
import core.entities.Environment
import core.entities.Optimizer
import core.scene.Scene
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class SceneImpl : Scene {
    private val entityMap = mutableMapOf<KClass<out Entity>, MutableList<Entity>>()
    private val agentMap = mutableMapOf<Int, Agent>()

    override val optimizer: Optimizer
        get() = getEntityList<Optimizer>().first()

    override val environment: Environment
        get() = getEntityList<Environment>().first()

    override val agents: Map<Int, Agent> = agentMap

    init {
        initEntityMap()
    }

    private fun initEntityMap() {
        entityMap[Optimizer::class] = mutableListOf()
        entityMap[Environment::class] = mutableListOf()
    }

    private inline fun <reified T : Entity> getEntityList(): List<T> {
        return entityMap.getValue(T::class) as List<T>
    }

    fun setOptimizer(optimizer: Optimizer) {
        addToEntityMap(Optimizer::class, optimizer)
    }

    fun setEnvironment(environment: Environment) {
        addToEntityMap(Environment::class, environment)
    }

    private fun addToEntityMap(kClass: KClass<out Entity>, entity: Entity) {
        entityMap.getValue(kClass).add(entity)
    }

    fun addAgent(id: Int, agent: Agent) {
        agentMap[id] = agent
    }

    fun removeAgentById(id: Int) {
        agentMap.remove(id)
    }

    fun getEntities(): List<Entity> = entityMap.values.flatten()
}