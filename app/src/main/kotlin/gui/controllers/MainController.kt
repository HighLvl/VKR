package gui.controllers

import app.services.ServiceManager
import app.services.provider.component.ComponentProvider
import gui.dockspace.Dockspace
import gui.dockspace.ScriptViewPortWindow
import gui.dockspace.ToolsWindow
import gui.dockspace.Window
import gui.model.ComponentRepository
import gui.model.LogService
import gui.utils.getString
import gui.view.*
import gui.viewmodel.AgentModelControlViewModel
import gui.viewmodel.LoggerViewModel
import gui.viewmodel.SceneViewModel

class MainController : Controller() {
    private val sceneViewModel = SceneViewModel(ServiceManager.sceneService, ComponentRepository(ComponentProvider))
    private val componentInspectorWindow =
        Window(getString("component_inspector_window_title"), ComponentInspectorView(sceneViewModel))
    private val objectTreeWindow = Window(getString("object_tree_window_title"), ObjectTreeView(sceneViewModel))
    private val loggerWindow = Window(getString("logger_window_title"), LoggerView(LoggerViewModel(LogService())))
    private val toolsWindow = ToolsWindow(
        MenuBarView(sceneViewModel),
        ModelControlView(AgentModelControlViewModel(ServiceManager.modelControlService))
    )
    private val scriptViewPortWindow = ScriptViewPortWindow(getString("script_view_window_title"), ScriptViewPortView(sceneViewModel))


    private val dockspace = Dockspace().apply {
        dockToolsWindow(toolsWindow)
        dock(loggerWindow, Dockspace.Position.LEFT_DOWN)
        dock(componentInspectorWindow, Dockspace.Position.RIGHT)
        dock(scriptViewPortWindow, Dockspace.Position.LEFT_UP_RIGHT)
        dock(objectTreeWindow, Dockspace.Position.LEFT_UP_LEFT)
    }

    override fun onPreRun() {
        dockspace.onPreRun()
        ServiceManager.start()
    }

    override fun update() {
        dockspace.draw()
    }

    override fun stop() {
        ServiceManager.stop()
    }
}