package app.components.user

import core.components.agent.AgentInterface
import core.components.base.AddInSnapshot
import core.components.base.Component
import core.entities.getComponent

import core.services.Services

class Position : Component {
    @AddInSnapshot(1)
    var rowPropName = ""
    @AddInSnapshot(2)
    var columnPropName = ""
    @AddInSnapshot(3)
    val row: Int
        get() = getValue(rowPropName)
    @AddInSnapshot(4)
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