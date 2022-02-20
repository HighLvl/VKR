package views.inspector.property

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import views.inspector.property.base.OnChangeValueListener
import views.inspector.property.base.PropertyBuilder

class OnChangeValueListenerImpl(
    private val parentNode: JsonNode,
    private val componentNode: JsonNode,
    private val propName: String,
    private val changedNodeProps: ArrayNode
) : OnChangeValueListener {
    override fun onChangeValue(newValue: Any) {
        updatePropValue(newValue)
    }

    private fun updatePropValue(newValue: Any) {
        when {
            parentNode.isArray -> {
                val parentArrayNode = parentNode as ArrayNode
                val index = propName.toInt()
                parentArrayNode.remove(index)
                parentArrayNode.insert(index, newValue)
                changedNodeProps.add(componentNode)

            }
            parentNode.isObject -> {
                val parentObjectNode = parentNode as ObjectNode
                parentObjectNode.put(propName, newValue)
                changedNodeProps.add(componentNode)
            }
        }
    }
}