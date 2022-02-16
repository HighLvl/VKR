package views.inspector.property

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode

class OnChangeValueListenerImpl(
    private val parentNodeType: PropertyBuilder.JsonNodeType,
    private val parentNode: JsonNode,
    private val componentNode: JsonNode,
    private val propName: String,
    private val changedNodeProps: ArrayNode
) :
    OnChangeValueListener {
    override fun onChangeValue(newValue: Any) {
        updatePropValue(newValue)
    }

    private fun updatePropValue(newValue: Any) {
        when (parentNodeType) {
            PropertyBuilder.JsonNodeType.COMPONENT -> {
                val parentObjectNode = parentNode as ObjectNode
                parentObjectNode.put("value", newValue)
                changedNodeProps.add(componentNode)
            }
            PropertyBuilder.JsonNodeType.ARRAY -> {
                val parentArrayNode = parentNode as ArrayNode
                val index = propName.toInt()
                parentArrayNode.remove(index)
                parentArrayNode.insert(index, newValue)
                changedNodeProps.add(componentNode)

            }
            PropertyBuilder.JsonNodeType.OBJECT -> {
                val parentObjectNode = parentNode as ObjectNode
                parentObjectNode.put(propName, newValue)
                changedNodeProps.add(componentNode)

            }
        }
    }
}