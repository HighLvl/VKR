package view

import imgui.ImGui
import viewmodel.*
import widgets.Widget
import widgets.inspector.component.AddComponentsWidget
import widgets.inspector.component.ComponentsWidget
import widgets.inspector.component.PropertyInspectorFactoryImpl

class ComponentInspectorView(private val viewModel: SceneViewModel) : View(), Widget {
    private val componentsWidget: ComponentsWidget = ComponentsWidget(
        PropertyInspectorFactoryImpl(
            viewModel::changeRequestBody,
            viewModel::commitRequestBody
        )
    )
    private val addComponentsWidget = AddComponentsWidget()
    private var title: String = ""

    override fun onPreRun() {
        viewModel.availableComponents.collectWithUiContext {
            addComponentsWidget.load(it, 0)
        }
        viewModel.selectedEntity.collectWithUiContext {
            onEntitySelected(it)
        }
        viewModel.components.collectWithUiContext {
            componentsWidget.components = it
        }
        componentsWidget.apply {
            onCloseComponent = viewModel::removeComponent
            onChangePropertyListener = viewModel::changeComponentProperties
        }
        addComponentsWidget.onSelectComponent = viewModel::addComponent
    }

    private fun onEntitySelected(entity: Entity) {
        title = when (entity) {
            is Environment -> {
                TITLE_COMPONENT_INSPECTOR.format(TITLE_ENVIRONMENT_OBJECT)
            }
            is Experimenter -> {
                TITLE_COMPONENT_INSPECTOR.format(TITLE_EXPERIMENTER_OBJECT)
            }
            is Agent -> {
                TITLE_COMPONENT_INSPECTOR.format(TITLE_AGENT_OBJECT.format(entity.type, entity.id))
            }
            is AgentPrototype -> {
                TITLE_COMPONENT_INSPECTOR.format(TITLE_AGENT_PROTOTYPE_OBJECT.format(entity.type))
            }
            is None -> {
                TITLE_OBJECT_NOT_SELECTED
            }
        }
        addComponentsWidget.apply {
            enabled = entity !is None
            hide()
        }
    }

    override fun draw() {
        drawTitle(title)
        repeat(4) { ImGui.spacing() }
        ImGui.separator()
        componentsWidget.draw()
        ImGui.separator()
        repeat(4) { ImGui.spacing() }
        addComponentsWidget.draw()
    }

    private fun drawTitle(title: String) {
        ImGui.text(title)
    }


    private companion object {
        const val TITLE_ENVIRONMENT_OBJECT = "Environment"
        const val TITLE_EXPERIMENTER_OBJECT = "Experimenter"
        const val TITLE_COMPONENT_INSPECTOR = "Inspected object: %s"
        const val TITLE_OBJECT_NOT_SELECTED = "The object is not selected"
        const val TITLE_AGENT_OBJECT = "%s (%d)"
        const val TITLE_AGENT_PROTOTYPE_OBJECT = "%s (prototype)"
    }
}

