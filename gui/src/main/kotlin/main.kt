import app.components.AgentInterface
import app.components.experiment.Experiment
import app.logger.Log
import app.logger.Logger
import app.scene.SceneImpl
import app.services.model.`interface`.modelInterface
import app.services.scene.SceneService
import controllers.SceneController
import core.coroutines.AppContext
import core.coroutines.launchWithAppContext
import core.entities.Agent
import core.entities.Entity
import imgui.ImGuiViewport
import imgui.app.Application
import imgui.app.Configuration
import imgui.callback.ImPlatformFuncViewport
import imgui.flag.ImGuiConfigFlags
import imgui.flag.ImGuiWindowFlags
import imgui.internal.ImGui
import kotlinx.coroutines.currentCoroutineContext
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
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class Main : Application() {
    private val loggerView = LoggerView()

    override fun configure(config: Configuration) {
        config.title = "Dear ImGui is Awesome!"
        config.isFullScreen = true
    }

    override fun initImGui(config: Configuration?) {
        super.initImGui(config)
        val io = ImGui.getIO()
        io.iniFilename = null // We don't want to save .ini file
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard) // Enable Keyboard Controls
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable) // Enable Docking
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable) // Enable Multi-Viewport / Platform Windows
        io.configViewportsNoTaskBarIcon = true

        ImGui.getIO().fonts.addFontFromMemoryTTF(
            loadFromResources("Roboto-Regular.ttf"),
            14f * SCALE_FACTOR
        )
        ImGui.getIO().fonts.build()
    }

    var time: Duration? = null

    @OptIn(ExperimentalTime::class)
    override fun preRun() {
        super.preRun()
        val logger = Logger
        launchWithAppContext {
            var count = 0
        }
        launchWithAppContext {
            logger.logs.collect {
                loggerView.log(it.text, it.level.mapToLogViewLevel())
            }
        }
    }

    private val loggerDockedWindow = Window("Logger", loggerView)
    private val scriptViewPortWindow = Window("ScriptView", ScriptViewPort {
    })

    data class A(val x: Float = 0f, val y: String = ";jlk")

    private val sceneService = SceneService().apply { start() }

    var entity: Entity? = run {
        val agent = Agent("Simple agent")
        val modelInterface = modelInterface {
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
        agent.setComponent(Experiment())
        agent.setComponent(object : core.components.base.Component() {
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

    private val sceneController = SceneController(sceneService.scene, componentInspector, objectTree)



    private val modelControlWindow = ModelControlView().apply {
        onClickConnectListener = { ip, port ->
            Logger.log("ip: $ip, port: $port", Log.Level.INFO)
            connect()
        }
        onClickPauseListener = { pause() }
        onClickRunListener = { run() }
        onClickResumeListener = { run() }
        onClickStopListener = { stop() }
        onClickDisconnectListener = {
            disconnect()
        }
    }

    private val dockspace = Dockspace().apply {
        dock(loggerDockedWindow, Dockspace.Position.LEFT_DOWN)
        dock(componentInspectorWindow, Dockspace.Position.RIGHT)
        dock(scriptViewPortWindow, Dockspace.Position.LEFT_UP_RIGHT)
        dock(objectTreeWindow, Dockspace.Position.LEFT_UP_LEFT)
    }

    private val toolsHeight = 50f
    override fun process() {
        sceneController.update()

        val viewport = ImGui.getMainViewport()
        ImGui.beginMainMenuBar()
        val menuBarHeight = ImGui.getWindowHeight()
        val menuBarWidth = ImGui.getWindowWidth()
        val menuBarPos = ImGui.getWindowPos()
        if(ImGui.beginMenu("File")) {
            if (ImGui.menuItem("Load configuration")) {

            }
            if (ImGui.menuItem("Clear scene")) {

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
        fun main(args: Array<String>) {
            runBlocking {
                AppContext.context = currentCoroutineContext()
                launch(Main())
            }
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


