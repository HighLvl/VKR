package views.properties

import imgui.ImGui

abstract class ObjectProperty(name: String) : Property(name) {
    protected val properties = mutableListOf<Property>()

    fun addProperty(property: Property) {
        properties.add(property)
    }

    override fun drawValue() {
        properties.forEach { it.draw() }
    }
}

open class NodeTreeObjectProperty(name: String) : ObjectProperty(name) {
    override fun draw() {
        ImGui.pushID(name)
        val opened = ImGui.treeNode(name, name)
        ImGui.nextColumn()
        ImGui.nextColumn()
        if (opened) {
            drawValue()
            ImGui.treePop()
        }
        ImGui.popID()
    }
}

class ListObjectProperty : ObjectProperty("") {
    override fun draw() {
        super.drawValue()
    }
}