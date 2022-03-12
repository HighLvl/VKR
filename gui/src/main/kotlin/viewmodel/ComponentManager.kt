package viewmodel

import app.components.*
import app.components.agent.AgentInterface
import app.components.agent.RequestBodies
import app.components.agent.RequestBody
import app.components.experiment.Experiment
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import core.components.base.Component
import app.components.base.SystemComponent

class ComponentManager {
    private val objectMapper = jacksonObjectMapper()
    private val idComponentMap = mutableMapOf<Int, Component>()
    private val idSnapshotNodeMap = mutableMapOf<Int, ObjectNode>()
    private val nameRequestBodyNodeMap = mutableMapOf<String, ObjectNode>()
    private lateinit var requestBodies: RequestBodies

    fun getComponentDtoList(components: List<Component>): List<ComponentDto> {
        idComponentMap.clear()
        idSnapshotNodeMap.clear()
        return components.mapIndexed { index, component ->
            idComponentMap[index] = component
            val compClassName = component::class.qualifiedName.toString()
            val snapshotNode = objectMapper.valueToTree<ObjectNode>(component.getSnapshot())
            idSnapshotNodeMap[index] = snapshotNode
            val propInspectorNode = snapshotNode.mapToPropertyInspectorNode(
                IMMUTABLE_PROPS, MUTABLE_PROPS
            )
            when (component) {
                is AgentInterface -> AgentInterface(
                    index,
                    compClassName,
                    propInspectorNode,
                    component.requestBodies.mapToPropBuilderNodes()
                )
                is Experiment -> Experiment(index, compClassName, propInspectorNode)
                is app.components.configuration.Configuration -> Configuration(index, compClassName, propInspectorNode)
                is SystemComponent -> UnknownComponent(index, compClassName, false, propInspectorNode)
                else -> UnknownComponent(index, compClassName, true, propInspectorNode)
            }
        }.toList()
    }

    fun changeComponentProperties(componentId: Int, propInspectorNode: ObjectNode) {
        val component = idComponentMap[componentId] ?: return
        val snapNode = propInspectorNode.mapToSnapshotNode(idSnapshotNodeMap[componentId]!!, MUTABLE_PROPS)
        component.loadSnapshot(objectMapper.treeToValue(snapNode))
    }

    fun getComponentById(id: Int) = idComponentMap[id]

    private fun ObjectNode.mapToPropertyInspectorNode(
        immutablePropsName: String,
        mutablePropsName: String
    ): ObjectNode {
        val immutableObjectNode = ObjectNode(JsonNodeFactory.instance).inflateWith(immutablePropsName, this)
        val mutableObjectNode = ObjectNode(JsonNodeFactory.instance).inflateWith(mutablePropsName, this)
        return ObjectNode(JsonNodeFactory.instance).apply {
            set<ObjectNode>(immutablePropsName, immutableObjectNode)
            set<ObjectNode>(mutablePropsName, mutableObjectNode)
        }
    }

    private fun ObjectNode.inflateWith(propsType: String, snapPropNode: ObjectNode): ObjectNode {
        for (item in snapPropNode[propsType].elements()) {
            val propName = item["name"].asText()
            val propValue = item["value"]
            set<JsonNode>(propName, propValue)
        }
        return this
    }

    private fun ObjectNode.mapToSnapshotNode(
        snapPropNodeInstance: ObjectNode,
        mutablePropsName: String
    ): ObjectNode {
        val snapPropNode = ObjectNode(JsonNodeFactory.instance)
        val snapMutableArrayNode = ArrayNode(JsonNodeFactory.instance)
        this[mutablePropsName].fields().forEach { (propName, valueNode) ->
            val propNode = ObjectNode(JsonNodeFactory.instance)
            propNode.put("name", propName)
            propNode.set<JsonNode>("value", valueNode)
            val type = snapPropNodeInstance[mutablePropsName].elements()
                .asSequence()
                .first { it["name"].asText() == propName }["type"].asText()
            propNode.put("type", type)
            snapMutableArrayNode.add(propNode)
        }
        snapPropNode.set<ArrayNode>(mutablePropsName, snapMutableArrayNode)
        return snapPropNode
    }

    private fun RequestBodies.mapToPropBuilderNodes(): List<Pair<String, ObjectNode>> {
        requestBodies = this
        return bodies.map { requestBody ->
            val requestBodyObjectNode = objectMapper.valueToTree<ObjectNode>(requestBody)
            nameRequestBodyNodeMap[requestBody.name] = requestBodyObjectNode
            val propBuilderObjectNode = requestBodyObjectNode.mapToPropBuilderObjectNode()
            requestBody.name to propBuilderObjectNode
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

    fun changeRequestBody(name: String, propBuilderObjectNode: ObjectNode) {
        val node = propBuilderObjectNode.mapToRequestBodyObjectNode(nameRequestBodyNodeMap[name]!!)
        val requestBody = objectMapper.treeToValue<RequestBody>(node)
        requestBodies.changeRequestBody(requestBody)
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

    fun commitRequestBody(name: String) {
        requestBodies.commit(name)
    }

    companion object {
        const val IMMUTABLE_PROPS = "immutableProps"
        const val MUTABLE_PROPS = "mutableProps"
    }
}