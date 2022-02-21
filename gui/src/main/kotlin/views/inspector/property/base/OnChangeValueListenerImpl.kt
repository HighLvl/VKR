package views.inspector.property.base

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import views.inspector.property.insert
import views.inspector.property.put

class OnChangeValueListenerImpl(
    private val parentNode: JsonNode,
    private val rootNodePropNamePair: Pair<String, JsonNode>,
    private val propName: String,
    private val changedNodeProps: ObjectNode
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
            }
            parentNode.isObject -> {
                val parentObjectNode = parentNode as ObjectNode
                parentObjectNode.put(propName, newValue)
            }
        }
        val (rootPropName, rootNode) = rootNodePropNamePair
        changedNodeProps.set<JsonNode>(rootPropName, rootNode[rootPropName])
    }
}