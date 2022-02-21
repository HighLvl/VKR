package views.inspector.property

import app.components.RequestBodies
import app.components.RequestBody
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import imgui.ImGui
import views.inspector.property.base.*
import views.inspector.splitOnCapitalLetters
import views.properties.ListObjectProperty
import views.properties.ObjectProperty
import views.properties.RequestBodyObjectProperty


class AgentInterfacePropertyInspector(private val requestBodies: RequestBodies) :
    PropertyInspector() {
    private val objectMapper = jacksonObjectMapper()

    init {
        immutablePropertyFactory = AgentInterfaceImmutablePropertyFactory()
    }

    override fun drawProperties() {
        super.drawProperties()
        if (requestBodies.bodies.isNotEmpty()) {
            ImGui.separator()
        }
        for (requestBody in requestBodies.bodies) {
            val requestBodyObjectNode = objectMapper.valueToTree<ObjectNode>(requestBody)
            val propBuilderObjectNode = requestBodyObjectNode.mapToPropBuilderObjectNode()
            val listenerFactory = AgentInterfaceListenerFactory(propBuilderObjectNode, requestBodyObjectNode)
            val factory = AgentInterfaceRequestPropertyFactory(listenerFactory, propBuilderObjectNode)
            val propBuilder = PropertyBuilder(factory)
            propBuilder.buildProperty(
                requestBody.name,
                propBuilderObjectNode[requestBody.name],
                propBuilderObjectNode
            ).draw()
        }
    }

    private fun ObjectNode.mapToPropBuilderObjectNode(): ObjectNode {
        val objectNode = ObjectNode(JsonNodeFactory.instance)

        val argObjectNode = ObjectNode(JsonNodeFactory.instance)
        (this["args"] as ObjectNode).fields().forEach { (paramName, arg) ->
            argObjectNode.set<JsonNode>(paramName, arg["first"])
        }
        objectNode.set<ObjectNode>(this["name"].asText(), argObjectNode)

        return objectNode
    }

    private fun ObjectNode.mapToRequestBodyObjectNode(instance: ObjectNode): ObjectNode {
        this.fields().forEach { (_, valueNode) ->
            valueNode.fields().forEach { (argName, argValueNode) ->
                val instanceNodeArgValue = instance["args"][argName] as ObjectNode
                instanceNodeArgValue.set<JsonNode>("first", argValueNode)
            }
        }
        return instance
    }

    private inner class AgentInterfaceListenerFactory(
        private val propBuilderObjectNode: ObjectNode,
        private val requestBodyObjectNode: ObjectNode
    ) : OnChangeValueListenerFactory {

        override fun create(
            parentNode: JsonNode,
            propName: String
        ): OnChangeValueListener {
            return object : OnChangeValueListener {
                override fun onChangeValue(newValue: Any) {
                    (parentNode as ObjectNode).put(propName, newValue)
                    val requestBodyNode =
                        propBuilderObjectNode.mapToRequestBodyObjectNode(requestBodyObjectNode)
                    val newRequestBody = objectMapper.treeToValue<RequestBody>(requestBodyNode)
                    requestBodies.changeRequestBody(newRequestBody)
                }
            }
        }
    }

    private inner class AgentInterfaceRequestPropertyFactory(
        onChangeValueListenerFactory: OnChangeValueListenerFactory,
        private val rootNode: JsonNode
    ) : MutablePropertyFactory(SimpleMutablePropertyFactory(), onChangeValueListenerFactory) {
        override fun createObjectProperty(name: String, parentNode: JsonNode): ObjectProperty = when (rootNode) {
            parentNode -> RequestBodyObjectProperty(name.splitOnCapitalLetters()) {
                requestBodies.commit(name)
            }
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


