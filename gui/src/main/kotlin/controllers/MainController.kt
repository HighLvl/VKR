package controllers

import app.logger.Log
import app.logger.Logger
import app.services.ServiceManager
import core.coroutines.launchWithAppContext
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiWindowFlags
import imgui.internal.ImGui
import imgui.type.ImBoolean
import views.*
import views.inspector.component.ComponentInspector
import views.inspector.component.PropertyInspectorFactoryImpl
import views.objecttree.ObjectTree

class MainController : Controller() {
    private val loggerView = LoggerView()
    private val loggerDockedWindow = Window("Logger", loggerView)

    private val componentInspector = ComponentInspector(PropertyInspectorFactoryImpl())
    private val componentInspectorWindow = Window("Inspector", componentInspector)

    private val objectTree = ObjectTree()
    private val objectTreeWindow = Window("Object tree", objectTree)
    private val sceneSetup = SceneSetup()

    private val sceneController =
        SceneController(ServiceManager.sceneService, componentInspector, objectTree, sceneSetup)

    private val modelControlWindow = ModelControlView()
    private val modelController = ModelController(modelControlWindow, ServiceManager.modelControlService)

    private val scriptViewPort = ScriptViewPort()
    private val scriptViewPortWindow = ScriptViewPortWindow("ScriptView", scriptViewPort)
    private val scriptViewPortController = ScriptViewPortController(scriptViewPort)

    private val toolsHeight = 50f
    private val isDarkTheme = ImBoolean()

    private val dockspace = Dockspace().apply {
        dock(loggerDockedWindow, Dockspace.Position.LEFT_DOWN)
        dock(componentInspectorWindow, Dockspace.Position.RIGHT)
        dock(scriptViewPortWindow, Dockspace.Position.LEFT_UP_RIGHT)
        dock(objectTreeWindow, Dockspace.Position.LEFT_UP_LEFT)
    }

    private val controllers = listOf(modelController, sceneController, scriptViewPortController)

    override fun start() {
        super.start()
        launchWithAppContext {
            Logger.logs.collect {
                loggerView.log(it.text, it.level.mapToLogViewLevel())
            }
        }
        controllers.forEach { it.start() }
        ServiceManager.start()
        setupImGuiStyle(false, 1f)
    }

    private fun Log.Level.mapToLogViewLevel() = when (this) {
        Log.Level.INFO -> LoggerView.Level.INFO
        Log.Level.ERROR -> LoggerView.Level.ERROR
        Log.Level.DEBUG -> LoggerView.Level.DEBUG
    }

    override fun update() {
        super.update()
        controllers.forEach { it.update() }

        val viewport = ImGui.getMainViewport()
        ImGui.beginMainMenuBar()
        val menuBarHeight = ImGui.getWindowHeight()
        val menuBarWidth = ImGui.getWindowWidth()
        val menuBarPos = ImGui.getWindowPos()
        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("Load configuration")) {
                sceneSetup.onLoadConfigurationListener(FileOpenDialog().open("kts"))
            }
            ImGui.endMenu()
        }
        if (ImGui.beginMenu("View")) {
            if (ImGui.checkbox("Dark theme", isDarkTheme)) {
                setupImGuiStyle(isDarkTheme.get())
            }
            ImGui.endMenu()
        }
        ImGui.endMainMenuBar()

        val flags = ImGuiWindowFlags.NoMove or
                ImGuiWindowFlags.NoBringToFrontOnFocus or
                ImGuiWindowFlags.NoResize or
                ImGuiWindowFlags.NoScrollbar or
                ImGuiWindowFlags.NoSavedSettings or
                ImGuiWindowFlags.NoTitleBar
        ImGui.setNextWindowSize(menuBarWidth, toolsHeight)
        ImGui.setNextWindowPos(menuBarPos.x, menuBarPos.y + menuBarHeight)
        if (ImGui.begin("Tools window", flags)) {
            val width = modelControlWindow.width
            ImGui.setWindowPos(
                menuBarPos.x + (menuBarWidth - width) / 2,
                menuBarPos.y + menuBarHeight
            )
            modelControlWindow.draw()
        }
        ImGui.end()

        ImGui.setNextWindowSize(viewport.sizeX, viewport.sizeY - menuBarHeight - toolsHeight)
        ImGui.setNextWindowPos(viewport.posX, viewport.posY + menuBarHeight + toolsHeight)
        ImGui.setNextWindowViewport(viewport.id)
        dockspace.height = viewport.sizeY
        dockspace.width = viewport.sizeX
        dockspace.draw()
    }

    override fun stop() {
        super.stop()
        ServiceManager.stop()
        controllers.forEach { it.stop() }
    }

    private fun setupImGuiStyle(bStyleDark_: Boolean, alpha_: Float = 1.0f) {
        val style = ImGui.getStyle()

        // light style from Pacôme Danhiez (user itamago) https://github.com/ocornut/imgui/pull/511#issuecomment-175719267
        style.alpha = 1.0f;
        style.frameRounding = 3.0f;
        style.setColor(ImGuiCol.Text, 0.00f, 0.00f, 0.00f, 1.00f)
        style.setColor(ImGuiCol.TextDisabled, 0.60f, 0.60f, 0.60f, 1.00f);
        style.setColor(ImGuiCol.WindowBg, 0.94f, 0.94f, 0.94f, 0.94f);
        style.setColor(ImGuiCol.ChildBg, 0.00f, 0.00f, 0.00f, 0.00f);
        style.setColor(ImGuiCol.PopupBg, 0.9f, 0.9f, 0.9f, 0.94f);
        style.setColor(ImGuiCol.Border, 0.00f, 0.00f, 0.00f, 0.39f);
        style.setColor(ImGuiCol.BorderShadow, 1.00f, 1.00f, 1.00f, 0.10f);
        style.setColor(ImGuiCol.FrameBg, 1f, 1f, 1f, 0.94f);
        style.setColor(ImGuiCol.FrameBgHovered, 0.26f, 0.59f, 0.98f, 0.40f);
        style.setColor(ImGuiCol.FrameBgActive, 0.26f, 0.59f, 0.98f, 0.67f);
        style.setColor(ImGuiCol.TitleBg, 0.9f, 0.9f, 0.9f, 0.94f);
        style.setColor(ImGuiCol.TitleBgCollapsed, 1.00f, 1.00f, 1.00f, 0.51f);
        style.setColor(ImGuiCol.TitleBgActive, 0.9f, 0.9f, 0.9f, 0.94f);
        style.setColor(ImGuiCol.MenuBarBg, 0.9f, 0.9f, 0.9f, 0.94f);
        style.setColor(ImGuiCol.ScrollbarBg, 0.98f, 0.98f, 0.98f, 0.53f);
        style.setColor(ImGuiCol.ScrollbarGrab, 0.69f, 0.69f, 0.69f, 1.00f);
        style.setColor(ImGuiCol.ScrollbarGrabHovered, 0.59f, 0.59f, 0.59f, 1.00f);
        style.setColor(ImGuiCol.ScrollbarGrabActive, 0.49f, 0.49f, 0.49f, 1.00f);
        // style.setColor(ImGuiCol.ComboBg, 0.86f, 0.86f, 0.86f, 0.99f);
        style.setColor(ImGuiCol.CheckMark, 0.26f, 0.59f, 0.98f, 1.00f);
        style.setColor(ImGuiCol.SliderGrab, 0.24f, 0.52f, 0.88f, 1.00f);
        style.setColor(ImGuiCol.SliderGrabActive, 0.26f, 0.59f, 0.98f, 1.00f);
        style.setColor(ImGuiCol.Button, 0.26f, 0.59f, 0.98f, 0.40f);
        style.setColor(ImGuiCol.ButtonHovered, 0.26f, 0.59f, 0.98f, 1.00f);
        style.setColor(ImGuiCol.ButtonActive, 0.06f, 0.53f, 0.98f, 1.00f);
        style.setColor(ImGuiCol.Tab, 0.26f, 0.59f, 0.98f, 0.40f)
        style.setColor(ImGuiCol.TabHovered, 0.26f, 0.59f, 0.98f, 1.00f)
        style.setColor(ImGuiCol.TabActive, 0.06f, 0.53f, 0.98f, 1.00f)
        style.setColor(ImGuiCol.TabUnfocused, 0.26f, 0.59f, 0.98f, 0.40f)
        style.setColor(ImGuiCol.TabUnfocusedActive, 0.26f, 0.59f, 0.98f, 0.40f)
        style.setColor(ImGuiCol.Header, 0.26f, 0.59f, 0.98f, 0.31f);
        style.setColor(ImGuiCol.HeaderHovered, 0.26f, 0.59f, 0.98f, 0.80f);
        style.setColor(ImGuiCol.HeaderActive, 0.26f, 0.59f, 0.98f, 1.00f);
        style.setColor(ImGuiCol.TableHeaderBg, 0.9f, 0.9f, 0.9f, 1.00f);
//    style.setColor(ImGuiCol.Column, 0.39f, 0.39f, 0.39f, 1.00f);
//    style.setColor(ImGuiCol.ColumnHovered, 0.26f, 0.59f, 0.98f, 0.78f);
//    style.setColor(ImGuiCol.ColumnActive, 0.26f, 0.59f, 0.98f, 1.00f);
        style.setColor(ImGuiCol.ResizeGrip, 1.00f, 1.00f, 1.00f, 0.50f);
        style.setColor(ImGuiCol.ResizeGripHovered, 0.26f, 0.59f, 0.98f, 0.67f);
        style.setColor(ImGuiCol.ResizeGripActive, 0.26f, 0.59f, 0.98f, 0.95f);
//    style.setColor(ImGuiCol.CloseButton, 0.59f, 0.59f, 0.59f, 0.50f);
//    style.setColor(ImGuiCol.CloseButtonHovered, 0.98f, 0.39f, 0.36f, 1.00f);
        //style.setColor(ImGuiCol.CloseButtonActive, 0.98f, 0.39f, 0.36f, 1.00f);
        style.setColor(ImGuiCol.PlotLines, 0.39f, 0.39f, 0.39f, 1.00f);
        style.setColor(ImGuiCol.PlotLinesHovered, 1.00f, 0.43f, 0.35f, 1.00f);
        style.setColor(ImGuiCol.PlotHistogram, 0.90f, 0.70f, 0.00f, 1.00f);
        style.setColor(ImGuiCol.PlotHistogramHovered, 1.00f, 0.60f, 0.00f, 1.00f);
        style.setColor(ImGuiCol.TextSelectedBg, 0.26f, 0.59f, 0.98f, 0.35f);
        //style.setColor(ImGuiCol.ModalWindowDarkening, 0.20f, 0.20f, 0.20f, 0.35f);

        if (bStyleDark_) {
            for (i in 0 until ImGuiCol.COUNT) {
                val col = style.colors[i]
                val hsv = FloatArray(4)
                ImGui.colorConvertRGBtoHSV(col, hsv)

                if (hsv[1] < 0.1f) {
                    hsv[2] = 1.0f - hsv[2]
                }
                ImGui.colorConvertHSVtoRGB(hsv, col)
                if (col[3] < 1.00f) {
                    col[3] *= alpha_
                }
                style.setColor(i, col[0], col[1], col[2], col[3])
            }
        } else {
            for (i in 0 until ImGuiCol.COUNT) {
                val col = style.colors[i]
                if (col[3] < 1.00f) {
                    col[0] *= alpha_
                    col[1] *= alpha_
                    col[2] *= alpha_
                    col[3] *= alpha_
                }
                style.setColor(i, col[0], col[1], col[2], col[3])
            }
        }
    }
}