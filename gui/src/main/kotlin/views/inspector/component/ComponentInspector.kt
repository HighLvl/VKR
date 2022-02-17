package views.inspector.component

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import core.components.base.Component
import views.View
import views.component.ComponentView

class ComponentInspector : View {
    private val componentJsonNodes = mutableMapOf<JsonNode, (difference: ObjectNode) -> Unit>()


    fun setComponentJsonNodesWithListeners(map: Map<JsonNode, (difference: ObjectNode) -> Unit>) {
        componentJsonNodes.clear()
        componentJsonNodes.putAll(map)
    }

    override fun draw() {
        componentJsonNodes.asSequence().forEach {(jsonNode, listener) ->
            val componentView = ComponentView().apply { inflate(jsonNode) }
            componentView.draw()
            if (componentView.changed()) {
                listener(componentView.getDifference())
            }
        }
    }
}