package app.services.scene

import core.components.base.Component
import core.entities.*
import core.services.scene.Scene
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class SceneImpl : Scene {
    private val entityMap = mutableMapOf<KClass<out Entity>, MutableList<Entity>>()
    private val agentMap = mutableMapOf<Int, Agent>()
    private val agentPrototypeMap = mutableMapOf<String, AgentPrototype>()

    override val experimenter: Experimenter
        get() = getEntityList<Experimenter>().first()

    override val environment: Environment
        get() = getEntityList<Environment>().first()

    override val agents: Map<Int, Agent>
        get() = agentMap

    override val agentPrototypes: Map<String, AgentPrototype>
        get() = agentPrototypeMap

    init {
        initEntityMap()
    }

    override fun findEntityByComponent(component: Component): Entity? =
        getEntities().firstOrNull { it.getComponents().contains(component) }

    override fun findAgentsThatHaving(vararg componentClass: KClass<out Component>): List<Agent> {
        return agentMap.values.filter { agent -> componentClass.all { agent.getComponent(it) != null } }
    }

    private fun initEntityMap() {
        entityMap[Experimenter::class] = mutableListOf()
        entityMap[Environment::class] = mutableListOf()
    }

    private inline fun <reified T : Entity> getEntityList(): List<T> {
        return entityMap.getValue(T::class) as List<T>
    }

    fun setExperimenter(experimenter: Experimenter) {
        addToEntityMap(Experimenter::class, experimenter)
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

    fun getEntities(): List<Entity> = entityMap.values.flatten() + agentMap.values + agentPrototypeMap.values

    fun putAgentPrototype(agentType: String, prototype: AgentPrototype) {
        agentPrototypeMap[agentType] = prototype
    }

    fun getAgentPrototype(agentType: String) = agentPrototypeMap[agentType]

    fun removeAgentPrototype(agentType: String) = agentPrototypeMap.remove(agentType)
}