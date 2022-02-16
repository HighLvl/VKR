package views.inspector.component

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import core.components.base.Component
import core.components.base.ComponentConverter
import core.components.base.ComponentSnapshot
import core.components.loadSnapshot
import core.entities.Entity
import views.View
import views.component.ComponentView

class ComponentInspector : View {
    private var entity: Entity? = null
    private val objectMapper = jacksonObjectMapper()


    fun setEntity(entity: Entity) {
        this.entity = entity
    }

    override fun draw() {
        val entity = entity ?: return
        val components = entity.getComponents()
        components.asSequence().forEach {
            bindComponentToView(it)
        }
    }

    private fun bindComponentToView(component: Component) {
        val componentView = createComponentView(component)
        componentView.draw()
        if (componentView.changed()) {
            val newSnapshot = objectMapper.treeToValue(componentView.getJsonNode(), ComponentSnapshot::class.java)
            component.loadSnapshot(newSnapshot)
        }
    }

    private fun createComponentView(component: Component): ComponentView {
        val snapshot = ComponentConverter.convertToComponentSnapshot(component)
        val snapshotJsonNode = objectMapper.valueToTree<ObjectNode>(snapshot)
        return ComponentView(component.getName()).apply { inflate(snapshotJsonNode) }
    }

    private fun Component.getName() =
        this::class.simpleName.toString().replace("([^_])([A-Z])".toRegex(), "$1 $2")
}