package views.inspector.property

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import views.inspector.property.base.OnChangeValueListener
import views.inspector.property.base.OnChangeValueListenerFactory
import views.inspector.property.base.PropertyBuilder

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