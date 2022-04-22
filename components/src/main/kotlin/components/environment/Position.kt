package components.environment

import core.components.agent.AgentInterface
import core.components.base.AddToSnapshot
import core.components.base.Component
import core.components.base.TargetEntity
import core.entities.Agent
import core.entities.getComponent

import core.services.Services

@TargetEntity(Agent::class)
class Position : Component() {
    @AddToSnapshot(1)
    var rowPropName = ""
    @AddToSnapshot(2)
    var columnPropName = ""
    @AddToSnapshot(3)
    val row: Int
        get() = getValue(rowPropName)
    @AddToSnapshot(4)
    val column: Int
        get() = getValue(columnPropName)

    private fun getValue(propName: String): Int {
        val entity = Services.scene.findEntityByComponent(this) ?: return 0
        val agentInterface = entity.getComponent<AgentInterface>() ?: return 0
        val value = agentInterface.props[propName]
        return if (value is Int) {
            value
        } else {
            0
        }
    }
}