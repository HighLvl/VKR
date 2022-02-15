package views.properties

import imgui.ImGui
import views.View

abstract class Property(protected val name: String) : View {
    override fun draw() {
        ImGui.text("      $name")
        ImGui.nextColumn()
        drawValue()
        ImGui.nextColumn()
    }

    protected abstract fun drawValue()

}