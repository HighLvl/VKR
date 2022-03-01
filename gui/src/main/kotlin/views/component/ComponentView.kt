package views.component

import app.utils.splitOnCapitalLetters
import imgui.ImGui
import imgui.flag.ImGuiTreeNodeFlags
import views.Decorator
import views.inspector.property.base.PropertyInspector

open class ComponentView(
    private val compClass: String,
    propertyInspector: PropertyInspector

) : Decorator(propertyInspector) {
    protected fun drawPropertyInspector() {
        super.draw()
    }

    override fun draw() {
        val componentId = compClass
        val componentName = getName(componentId)
        ImGui.pushID(componentId)
        val flags = ImGuiTreeNodeFlags.DefaultOpen
        if (ImGui.collapsingHeader(componentName, flags)) {
            drawPropertyInspector()
        }
        ImGui.popID()
    }

    protected fun getName(componentId: String) = componentId.split(".")
        .last()
        .splitOnCapitalLetters()
}