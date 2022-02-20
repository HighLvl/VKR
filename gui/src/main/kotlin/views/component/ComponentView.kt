package views.component

import imgui.ImGui
import imgui.flag.ImGuiTreeNodeFlags
import views.Decorator
import views.inspector.property.base.PropertyInspector
import views.inspector.splitOnCapitalLetters

class ComponentView(
    private val compClass: String,
    propertyInspector: PropertyInspector
) : Decorator(propertyInspector) {

    override fun draw() {
        val componentId = compClass
        val componentName = getName(componentId)
        ImGui.pushID(componentId)
        if (ImGui.collapsingHeader(componentName, ImGuiTreeNodeFlags.DefaultOpen)) {
            super.draw()
        }
        ImGui.popID()
    }

    private fun getName(componentId: String) = componentId.split(".")
        .last()
        .splitOnCapitalLetters()
}