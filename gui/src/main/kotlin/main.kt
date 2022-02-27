import app.components.AgentInterface
import app.logger.Log
import app.logger.Logger
import app.scene.SceneImpl
import app.services.model.configuration.modelConfiguration
import app.services.model.control.AgentModelControlService
import app.services.scene.SceneService
import controllers.ModelController
import controllers.SceneController
import controllers.SceneSetup
import controllers.ScriptViewPortController
import core.api.AgentModelApiClient
import core.api.dto.*
import core.components.Component
import core.components.Script
import core.coroutines.AppContext
import core.coroutines.launchWithAppContext
import core.entities.Agent
import core.entities.Entity
import imgui.ImFontConfig
import imgui.ImFontGlyphRangesBuilder
import imgui.ImGuiIO
import imgui.ImGuiViewport
import imgui.app.Application
import imgui.app.Configuration
import imgui.callback.ImPlatformFuncViewport
import imgui.extension.implot.ImPlot
import imgui.extension.implot.ImPlotContext
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiConfigFlags
import imgui.flag.ImGuiWindowFlags
import imgui.internal.ImGui
import imgui.type.ImBoolean
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.lwjgl.glfw.GLFW
import views.*
import views.inspector.component.ComponentInspector
import views.inspector.component.PropertyInspectorFactoryImpl
import views.objecttree.ObjectTree
import java.io.IOException
import java.net.URISyntaxException
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class Main : Application() {
    private val loggerView = LoggerView()

    override fun configure(config: Configuration) {
        config.title = "Dear ImGui is Awesome!"
        config.isFullScreen = true
    }

    private lateinit var imPlotContext: ImPlotContext

    override fun initImGui(config: Configuration?) {
        super.initImGui(config)
        val io = ImGui.getIO()
        io.iniFilename = null // We don't want to save .ini file
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard) // Enable Keyboard Controls
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable) // Enable Docking
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable) // Enable Multi-Viewport / Platform Windows
        io.configViewportsNoTaskBarIcon = true

        initFonts(io)
        setupImGuiStyle(false, 1f)
        imPlotContext = ImPlot.createContext()
    }

    private fun initFonts(io: ImGuiIO) {
        val fontConfig = ImFontConfig()
        io.fonts.addFontFromMemoryTTF(
            loadFromResources("Roboto-Regular.ttf"),
            14f * SCALE_FACTOR,
            fontConfig
        )
        io.fonts.build()
        fontConfig.destroy()
    }

    override fun dispose() {
        super.dispose()
        ImPlot.destroyContext(imPlotContext)
    }

    var time: Duration? = null

    @OptIn(ExperimentalTime::class)
    override fun preRun() {
        super.preRun()
        launchWithAppContext {
            Logger.logs.collect {
                loggerView.log(it.text, it.level.mapToLogViewLevel())
            }
        }
    }

    private val loggerDockedWindow = Window("Logger", loggerView)
    private val scriptViewPort = ScriptViewPort()
    private val scriptViewPortWindow = ScriptViewPortWindow("ScriptView", scriptViewPort)

    data class A(val x: Float = 0f, val y: String = ";jlk")

    private val sceneService = SceneService().apply { start() }

    var entity: Entity? = run {
        val agent = Agent("Simple agent")
        val modelInterface = modelConfiguration {
            agentInterface("SimpleAgent") {
                setter<Int>("x")
                setter<String>("text")
                setter<A>("a")

                request<Unit>("putToMap") {
                    param<Int>("x")
                    param<Float>("y")
                }
            }
        }
        val agentInterface = AgentInterface(
            modelInterface.agentInterfaces.first().setters.toList(),
            modelInterface.agentInterfaces.first().requestSignatures.toList()
        )
        agent.setComponent(
            agentInterface
        )
        agent.setComponent(object : Component(), Script {
            var x = 0f
            var y = 0
            val name = "position"
            var mName = "mName"
        })
        val scene = sceneService.scene as SceneImpl
        scene.addAgent(0, agent)
        agent
    }


    private val componentInspector = ComponentInspector(PropertyInspectorFactoryImpl())
    val componentInspectorWindow = Window("Inspector", componentInspector)
    private val objectTree = ObjectTree()
    val objectTreeWindow = Window("Object tree", objectTree)
    val sceneSetup = SceneSetup()

    private val sceneController =
        SceneController(sceneService, componentInspector, objectTree, sceneSetup)


    private val modelControlWindow = ModelControlView()
    private val modelController = ModelController(modelControlWindow, agentMOdelControlService.apply { start() })

    private val scriptViewPortController = ScriptViewPortController(scriptViewPort, sceneService.scene)


    private val dockspace = Dockspace().apply {
        dock(loggerDockedWindow, Dockspace.Position.LEFT_DOWN)
        dock(componentInspectorWindow, Dockspace.Position.RIGHT)
        dock(scriptViewPortWindow, Dockspace.Position.LEFT_UP_RIGHT)
        dock(objectTreeWindow, Dockspace.Position.LEFT_UP_LEFT)
    }

    private val toolsHeight = 50f
    private val isDarkTheme = ImBoolean()

    override fun process() {
        sceneController.update()

        val viewport = ImGui.getMainViewport()
        ImGui.beginMainMenuBar()
        val menuBarHeight = ImGui.getWindowHeight()
        val menuBarWidth = ImGui.getWindowWidth()
        val menuBarPos = ImGui.getWindowPos()
        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("Load configuration")) {
                sceneSetup.onLoadConfigurationListener(FileOpenDialog().open("kts"))
            }
            if (ImGui.menuItem("Clear scene")) {
                sceneSetup.onClearSceneListener()
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

        loggerDockedWindow.draw()
        componentInspectorWindow.draw()
        scriptViewPortWindow.draw()
        objectTreeWindow.draw()
    }

    object OnChangedViewportListener : ImPlatformFuncViewport() {
        private var scaled = false
        override fun accept(p0: ImGuiViewport) {
            if (!scaled) {
                ImGui.getStyle().scaleAllSizes(SCALE_FACTOR)
                scaled = true
            }
        }
    }

    override fun run() {
        runBlocking {
            while (!GLFW.glfwWindowShouldClose(handle)) {
                runFrame()
                yield()
            }
        }
    }

    companion object {
        const val SCALE_FACTOR = 2f

        @JvmStatic
        fun main(args: Array<String>): Unit = runBlocking {
            AppContext.context = currentCoroutineContext()
            launch(Main())
            exitProcess(0)
        }


        private fun loadFromResources(name: String): ByteArray {
            return try {
                Files.readAllBytes(Paths.get(Main::class.java.getResource(name)!!.toURI()))
            } catch (e: IOException) {
                throw RuntimeException(e)
            } catch (e: URISyntaxException) {
                throw RuntimeException(e)
            }
        }
    }
}

fun Log.Level.mapToLogViewLevel() = when (this) {
    Log.Level.INFO -> LoggerView.Level.INFO
    Log.Level.ERROR -> LoggerView.Level.ERROR
    Log.Level.DEBUG -> LoggerView.Level.DEBUG
}

fun setupImGuiStyle(bStyleDark_: Boolean, alpha_: Float = 1.0f) {
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

val agentMOdelControlService = AgentModelControlService(object : AgentModelApiClient {
    override suspend fun connect(ip: String, port: Int): State {
        delay(500)
        Logger.log("Connected to $ip:$port", Log.Level.DEBUG)
        return State.STOP
    }

    override suspend fun disconnect() {
        delay(500)
        Logger.log("Disconnected", Log.Level.DEBUG)
    }

    override suspend fun run(globalArgs: GlobalArgs) {
        Logger.log("Request run", Log.Level.DEBUG)
        delay(1000)
        Logger.log("Requested run", Log.Level.DEBUG)
    }

    override suspend fun runAndSubscribeOnUpdate(globalArgs: GlobalArgs): Flow<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun callBehaviourFunctions(behaviour: Behaviour) {
        TODO("Not yet implemented")
    }

    var count = 0

    val timeSeq = sequence {
        var time = 0f
        while (true) {
            yield(time)
            time += 0.1f
        }
    }.iterator()

    override suspend fun requestSnapshot(): Snapshot {
        Logger.log("Request snapshot", Log.Level.DEBUG)
        delay(10)
        val time = timeSeq.next()
        val snapshot = when (count) {
            0 -> Snapshot(
                time, mutableListOf(
                    AgentSnapshot(1, mutableMapOf("a" to 3, "b" to 7), mutableListOf()),
                    AgentSnapshot(2, mutableMapOf(), mutableListOf()),
                    AgentSnapshot(3, mutableMapOf(), mutableListOf())
                ), mutableListOf()
            )
            1 -> Snapshot(
                time, mutableListOf(
                    AgentSnapshot(1, mutableMapOf(), mutableListOf()),
                    AgentSnapshot(2, mutableMapOf(), mutableListOf()),
                    AgentSnapshot(3, mutableMapOf(), mutableListOf()),
                    AgentSnapshot(4, mutableMapOf(), mutableListOf()),

                    ), mutableListOf()
            )
            2 -> Snapshot(
                time, mutableListOf(
                    AgentSnapshot(2, mutableMapOf(), mutableListOf()),
                    AgentSnapshot(3, mutableMapOf(), mutableListOf())
                ), mutableListOf()
            )
            else -> Snapshot(
                time, mutableListOf(
                    AgentSnapshot(1, mutableMapOf(), mutableListOf()),
                    AgentSnapshot(56, mutableMapOf(), mutableListOf()),
                    AgentSnapshot(3, mutableMapOf(), mutableListOf())
                ), mutableListOf()
            )
        }
        count++
        return snapshot
    }

    override suspend fun pause() {
        Logger.log("Request pause", Log.Level.DEBUG)
    }

    override suspend fun resume() {
        Logger.log("Request resume", Log.Level.DEBUG)
    }

    override suspend fun stop() {
        Logger.log("Request stop", Log.Level.DEBUG)
    }
})
