package core.services

import core.components.agent.AgentInterface
import core.components.agent.Props
import core.components.agent.request
import core.components.configuration.InputArgs
import core.components.model.SnapshotInfo
import core.entities.Agent
import core.entities.getComponent

/** Получить агента по id
 */
fun getAgent(agentId: Int) = Services.scene.agents[agentId]
/** Получить коллекцию агентов модели
 */
fun getAgents(): Collection<Agent> = Services.scene.agents.values
/** Получить Map, в которой id агента соответсвует агент
 */
fun getAgentsToIdMap() = Services.scene.agents
/** Получить Map, в которой имени параметра агента соответсвует его значение
 */
val Agent.props: Props
    get() = this.getComponent<AgentInterface>()!!.props

/** Получить значение параметра агента, приведенное к типу T
 */
inline fun <reified T : Any> Agent.getPropValue(propName: String): T? {
    val propValue = props[propName]
    if (propValue !is T) return null
    return propValue
}

/** Получить значение параметра агента, приведенное к типу T, по id агента
 */
inline fun <reified T : Any> getPropValue(agentId: Int, propName: String): T? {
    return getAgent(agentId)?.getPropValue(propName)
}

/** Запланировать запрос к агенту и подписаться на ответ
 */
inline fun <reified T : Any> Agent.request(name: String, args: List<Any>, noinline onResult: suspend (Result<T>) -> Unit = {}) {
    getComponent<AgentInterface>()!!.request(name, args, onResult)
}

/** Запланировать запрос к агенту по id агента и подписаться на ответ
 */
inline fun <reified T : Any> request(
    agentId: Int,
    name: String,
    args: List<Any>,
    noinline onResult: suspend (Result<T>) -> Unit = {}
) {
    getAgent(agentId)?.getComponent<AgentInterface>()?.request(name, args, onResult)
}

/** Запланировать запрос на установку значения переменной агента и подписать на ответ (успех или не успех).
 * @param varName имя переменной
 */
fun requestSetValue(agentId: Int, varName: String, value: Any, onResult: suspend (Result<Unit>) -> Unit = {}) {
    getAgent(agentId)?.getComponent<AgentInterface>()?.requestSetValue(varName, value, onResult)
}

/** Изменить входной аргумент модели. Должна быть загружена конфигурация модели с описанием входных аргументов.
 */
fun putInputArg(name: String, value: Any) {
    val inputArgs = Services.scene.environment.getComponent<InputArgs>()!!
    inputArgs.put(name, value)
}

/** Получить значение входного аргумента модели.
 */
fun <T: Any> getInputArg(name: String): T {
    val inputArgs = Services.scene.environment.getComponent<InputArgs>()!!
    return inputArgs.get(name)
}

/** Модельное время последнего снимка состояния.
 */
val modelTime: Double by Services.scene.environment.getComponent<SnapshotInfo>()!!::modelTime

/** Разница между модельными временами предыдущего и текущего снимков состояния модели
 */
val dt: Double by Services.scene.environment.getComponent<SnapshotInfo>()!!::dt

