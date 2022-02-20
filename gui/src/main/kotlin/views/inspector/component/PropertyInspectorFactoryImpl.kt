package views.inspector.component

import app.components.AgentInterface
import app.components.RequestBody
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import core.components.base.Component
import views.inspector.property.AgentInterfacePropertyInspector
import views.inspector.property.base.PropertyInspector

class PropertyInspectorFactoryImpl : ComponentInspector.PropertyInspectorFactory {
    override fun create(component: Component): PropertyInspector = when (component) {
        is AgentInterface -> AgentInterfacePropertyInspector(component.requestBodies)
        else -> PropertyInspector()
    }
}