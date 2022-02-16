package views.inspector.property

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode

class OnChangeValueListenerFactoryImpl(private val componentNode: JsonNode, private val changedNodeProps: ArrayNode) :
    OnChangeValueListenerFactory {
    override fun create(
        parentNodeType: PropertyBuilder.JsonNodeType,
        parentNode: JsonNode,
        propName: String
    ): OnChangeValueListener {
        return OnChangeValueListenerImpl(
            parentNodeType,
            parentNode,
            componentNode,
            propName,
            changedNodeProps
        )
    }
}