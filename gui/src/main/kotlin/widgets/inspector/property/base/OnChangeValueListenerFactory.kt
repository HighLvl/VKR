package widgets.inspector.property.base

import com.fasterxml.jackson.databind.JsonNode

interface OnChangeValueListenerFactory {
    fun create(
        parentNode: JsonNode,
        propName: String
    ): OnChangeValueListener
}