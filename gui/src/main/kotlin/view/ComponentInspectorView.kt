package view

import imgui.ImGui
import utils.getString
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
                getString("component_inspector_title", TITLE_ENVIRONMENT_OBJECT)
            }
            is Experimenter -> {
                getString("component_inspector_title", TITLE_EXPERIMENTER_OBJECT)
            }
            is Agent -> {
                getString("component_inspector_title", getString("agent_object_title", entity.type, entity.id))
            }
            is AgentPrototype -> {
                getString("component_inspector_title", getString("agent_prototype_title", entity.type))
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
        val TITLE_ENVIRONMENT_OBJECT = getString("environment_title")
        val TITLE_EXPERIMENTER_OBJECT = getString("experimenter_title")
        val TITLE_OBJECT_NOT_SELECTED = getString("object_not_selected")
    }
}

