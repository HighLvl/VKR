package views.inspector.component

import core.components.base.ComponentConverter
import core.entities.Entity
import imgui.ImGui
import views.View
import views.component.Component

class ComponentInspector : View {
    private var entity: Entity? = null

    fun setEntity(entity: Entity) {
        this.entity = entity
    }

    override fun draw() {
        val entity = entity
        entity ?: return
        val components = entity.getComponents()
        components.asSequence().map {
            ComponentConverter.convertToComponentSnapshot(it)
            Component(it::class.simpleName.toString())

        }.forEach {
            it.draw()
        }
    }
}