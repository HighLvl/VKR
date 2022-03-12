package model

import app.services.provider.component.ComponentProvider
import core.components.base.Component

import kotlin.reflect.KClass

class ComponentRepository(private val componentProvider: ComponentProvider) {
    private val componentTree = mutableMapOf<Int, KClass<out Component>>()
    private val components = componentProvider.getComponents()

    fun getComponentById(id: Int) = componentTree[id]

    fun getComponentTree(): Map<Int, Node> {
        val userComponents = components.user
        val appComponents = components.app
        val userNodes = mutableListOf<Int>()
        val tree = mutableMapOf<Int, Node>(
            0 to FolderNode("Components", listOf(1)),
            1 to FolderNode("User Components", userNodes)
        )
        for (i in userComponents.indices) {
            val id = i + 2
            userNodes.add(id)
            tree[id] = ComponentNode(userComponents[i].qualifiedName.toString())
            componentTree[id] = userComponents[i]
        }
        return tree
    }
}

sealed interface Node {
    val name: String
}

data class ComponentNode(override val name: String) : Node
data class FolderNode(override val name: String, val nodes: List<Int>) : Node