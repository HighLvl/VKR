package views.properties

import imgui.ImGui
import java.util.*

class ObjectProperty(name: String) : Property(name) {
    private val properties = mutableListOf<Property>()

    fun addProperty(property: Property) {
        properties.add(property)
    }

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

    override fun drawValue() {
        properties.forEach { it.draw() }

    }
}