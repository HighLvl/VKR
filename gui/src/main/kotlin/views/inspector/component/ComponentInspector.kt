package views.inspector.component

import app.components.SystemComponent
import app.components.getSnapshot
import app.components.loadSnapshot
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import core.components.base.Component
import imgui.ImGui
import views.View
import views.component.CloseableComponentView
import views.component.ComponentView
import views.inspector.property.base.PropertyInspector

class ComponentInspector(
    private val propertyInspectorFactory: PropertyInspectorFactory
) : View {
    var title = ""
    var components: List<Component> = listOf()
    var onCloseComponent: (Component) -> Unit = {}

    private val objectMapper = jacksonObjectMapper()

    override fun draw() {
        drawHeader()
        drawComponents()
    }

    private fun drawHeader() {
        ImGui.text(title)
        repeat(4) { ImGui.spacing() }
        ImGui.separator()
    }

    private fun drawComponents() {
        for (component in components) {
            val compClassName = component::class.qualifiedName.toString()
            val snapshotNode = objectMapper.valueToTree<ObjectNode>(component.getSnapshot())
            val propInspectorNode = snapshotNode.mapToPropertyInspectorNode()
            val propertyInspector = propertyInspectorFactory.create(component).apply { setPropNode(propInspectorNode) }
            val componentView = when (component) {
                is SystemComponent -> ComponentView(compClassName, propertyInspector)
                else -> CloseableComponentView(compClassName, propertyInspector) {
                    onCloseComponent(component)
                }
            }
            componentView.draw()
            if (propertyInspector.changed()) {
                val dif = propertyInspector.getDifference().mapToSnapPropNode(snapshotNode)
                component.loadSnapshot(objectMapper.treeToValue(dif))
            }
        }
    }

    private fun ObjectNode.mapToPropertyInspectorNode(): ObjectNode {
        val immutableObjectNode = ObjectNode(JsonNodeFactory.instance).inflate(IMMUTABLE_PROPS, this)
        val mutableObjectNode = ObjectNode(JsonNodeFactory.instance).inflate(MUTABLE_PROPS, this)
        return ObjectNode(JsonNodeFactory.instance).apply {
            set<ObjectNode>(IMMUTABLE_PROPS, immutableObjectNode)
            set<ObjectNode>(MUTABLE_PROPS, mutableObjectNode)
        }
    }

    private fun ObjectNode.inflate(propsType: String, snapPropNode: ObjectNode): ObjectNode {
        snapPropNode[propsType].elements().forEach {
            val propName = it["name"].asText()
            val propValue = it["value"]
            set<JsonNode>(propName, propValue)
        }
        return this
    }

    private fun ObjectNode.mapToSnapPropNode(snapPropNodeInstance: ObjectNode): ObjectNode {
        val snapPropNode = ObjectNode(JsonNodeFactory.instance)
        val snapMutableArrayNode = ArrayNode(JsonNodeFactory.instance)
        this[MUTABLE_PROPS].fields().forEach { (propName, valueNode) ->
            val propNode = ObjectNode(JsonNodeFactory.instance)
            propNode.put("name", propName)
            propNode.set<JsonNode>("value", valueNode)
            val type = snapPropNodeInstance[MUTABLE_PROPS].elements()
                .asSequence()
                .first { it["name"].asText() == propName }["type"].asText()
            propNode.put("type", type)
            snapMutableArrayNode.add(propNode)
        }
        snapPropNode.set<ArrayNode>(MUTABLE_PROPS, snapMutableArrayNode)
        return snapPropNode
    }

    interface PropertyInspectorFactory {
        fun create(component: Component): PropertyInspector
    }

    private companion object {
        const val IMMUTABLE_PROPS = "immutableProps"
        const val MUTABLE_PROPS = "mutableProps"
    }
}

