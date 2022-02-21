package views.inspector.property.base

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode

class OnChangeValueListenerFactoryImpl(private val componentNode: JsonNode, private val changedNodeProps: ArrayNode) :
    OnChangeValueListenerFactory {
    override fun create(
        parentNode: JsonNode,
        propName: String
    ): OnChangeValueListener {
        return OnChangeValueListenerImpl(
            parentNode,
            componentNode,
            propName,
            changedNodeProps
        )
    }
}