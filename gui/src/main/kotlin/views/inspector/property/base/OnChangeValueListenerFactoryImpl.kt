package views.inspector.property.base

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode

class OnChangeValueListenerFactoryImpl(private val rootNodePropNamePair: Pair<String, JsonNode>, private val changedNodeProps: ObjectNode) :
    OnChangeValueListenerFactory {
    override fun create(
        parentNode: JsonNode,
        propName: String
    ): OnChangeValueListener {
        return OnChangeValueListenerImpl(
            parentNode,
            rootNodePropNamePair,
            propName,
            changedNodeProps
        )
    }
}