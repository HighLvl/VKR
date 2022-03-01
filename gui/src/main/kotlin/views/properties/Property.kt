package views.properties

import app.utils.splitOnCapitalChars
import app.utils.uppercaseFirstChar
import imgui.ImGui
import views.View

abstract class Property(name: String) : View {
    protected val name = name.splitOnCapitalChars().uppercaseFirstChar()
    override fun draw() {
        ImGui.text("      $name")
        ImGui.nextColumn()
        drawValue()
        ImGui.nextColumn()
    }

    protected abstract fun drawValue()

}