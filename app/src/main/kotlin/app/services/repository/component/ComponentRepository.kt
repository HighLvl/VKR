package app.services.repository.component

import core.components.base.Component
import core.entities.Entity
import gui.utils.getString

import kotlin.reflect.KClass

class ComponentRepository {
    private val componentTree = mutableMapOf<Int, KClass<out Component>>()

    fun getComponentById(id: Int) = componentTree[id]

    fun getComponentTree(entity: Entity): Map<Int, Node> {
        val components = ComponentProvider.getAvailableComponents(entity)
        val userComponents = components.user
        val systemComponents = components.system
        val systemNodes = mutableListOf<Int>()
        val userNodes = mutableListOf<Int>()
        val tree = mutableMapOf<Int, Node>()
        val lastId = inflateComponentTrees(userComponents, 3, userNodes, tree, USER_COMPONENT_NAME_PREFIX)
        inflateComponentTrees(systemComponents, lastId + 1, systemNodes, tree, SYSTEM_COMPONENT_NAME_PREFIX)
        tree[0] = FolderNode(COMPONENTS, systemNodes + userNodes)
        return tree
    }

    private fun inflateComponentTrees(
        components: List<KClass<out Component>>,
        startId: Int,
        nodes: MutableList<Int>,
        nodeTree: MutableMap<Int, Node>,
        prefix: String
    ): Int {
        var id = startId
        for (i in components.indices) {
            id += i
            nodes.add(id)
            nodeTree[id] = ComponentNode(components[i].qualifiedName.toString().removePrefix(prefix))
            componentTree[id] = components[i]
        }
        return id
    }

    private companion object {
        val COMPONENTS = getString("components_title")
        const val SYSTEM_COMPONENT_NAME_PREFIX = "components."
        const val USER_COMPONENT_NAME_PREFIX = ""
    }
}

sealed interface Node {
    val name: String
}

data class ComponentNode(override val name: String) : Node
data class FolderNode(override val name: String, val nodes: List<Int>) : Node