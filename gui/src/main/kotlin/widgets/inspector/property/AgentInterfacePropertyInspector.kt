package widgets.inspector.property

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import imgui.ImGui
import widgets.inspector.property.base.*
import widgets.properties.ListObjectProperty
import widgets.properties.ObjectProperty
import widgets.properties.RequestBodyObjectProperty

class AgentInterfacePropertyInspector(private val requestBodies: List<Pair<String, ObjectNode>>) :
    PropertyInspector() {
    var onChangeValueListener: (String, ObjectNode) -> Unit = { _, _ -> }
    var onCommitListener: (String) -> Unit = {}

    init {
        immutablePropertyFactory = AgentInterfaceImmutablePropertyFactory()
    }

    override fun drawProperties() {
        super.drawProperties()
        drawRequestProperties()
    }

    private fun drawRequestProperties() {
        if (requestBodies.isNotEmpty()) {
            ImGui.separator()
        }
        for ((name, requestBody) in requestBodies) {
            val listenerFactory = AIOnChangeValueListenerFactory(name, requestBody)
            val propertyFactory = AIRequestPropertyFactory(listenerFactory, requestBody)
            PropertyBuilder(propertyFactory).buildProperty(name, requestBody[name], requestBody).draw()
        }
    }


    private inner class AIOnChangeValueListenerFactory(
        private val requestName: String,
        private val requestBodyObjectNode: ObjectNode
    ) : OnChangeValueListenerFactory {

        override fun create(
            parentNode: JsonNode,
            propName: String
        ): OnChangeValueListener {
            return object : OnChangeValueListener {
                override fun onChangeValue(newValue: Any) {
                    (parentNode as ObjectNode).put(propName, newValue)
                    onChangeValueListener(requestName, requestBodyObjectNode)
                }
            }
        }
    }

    private inner class AIRequestPropertyFactory(
        onChangeValueListenerFactory: OnChangeValueListenerFactory,
        private val rootNode: JsonNode
    ) : MutablePropertyFactory(SimpleMutablePropertyFactory(), onChangeValueListenerFactory) {
        override fun createObjectProperty(name: String, parentNode: JsonNode): ObjectProperty = when (rootNode) {
            parentNode -> RequestBodyObjectProperty(name) { onCommitListener(name) }
            else -> super.createObjectProperty(name, parentNode)
        }

    }

    inner class AgentInterfaceImmutablePropertyFactory : SimpleImmutablePropertyFactory() {
        override fun createObjectProperty(name: String, parentNode: JsonNode): ObjectProperty {
            if (name == AGENT_PROPS_NAME && parentNode == node[IMMUTABLE_PROPS]) {
                return ListObjectProperty()
            }
            return super.createObjectProperty(name, parentNode)
        }
    }

    private companion object {
        const val AGENT_PROPS_NAME = "props"
    }
}


