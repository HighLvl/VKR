package widgets.component

import core.utils.splitOnCapitalChars
import imgui.ImGui
import imgui.flag.ImGuiTreeNodeFlags
import widgets.Decorator
import widgets.inspector.property.base.PropertyInspector

open class ComponentWidget(
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
        .splitOnCapitalChars()
}