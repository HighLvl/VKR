package views.inspector.property

import com.fasterxml.jackson.databind.JsonNode

interface OnChangeValueListenerFactory {
    fun create(
        parentNodeType: PropertyBuilder.JsonNodeType,
        parentNode: JsonNode,
        propName: String
    ): OnChangeValueListener
}