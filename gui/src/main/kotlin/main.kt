import app.components.AgentInterfaceScript
import app.services.logger.Log
import app.services.logger.Logger
import core.coroutines.AppContext
import core.coroutines.launchWithAppContext
import core.entities.Agent
import imgui.ImGuiViewport
import imgui.app.Application
import imgui.app.Configuration
import imgui.callback.ImPlatformFuncViewport
import imgui.flag.ImGuiConfigFlags
import imgui.internal.ImGui
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.lwjgl.glfw.GLFW
import views.Dockspace
import views.LoggerView
import views.ScriptViewPort
import views.Window
import views.inspector.component.ComponentInspector
import views.inspector.property.PropertyInspector
import java.io.IOException
import java.net.URISyntaxException
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class Main : Application() {
    private val loggerView = LoggerView()

    override fun configure(config: Configuration) {
        config.title = "Dear ImGui is Awesome!"
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
        val logger = Logger()
        launchWithAppContext {
            var count = 0
            while (true) {
                count++
                time = measureTime {
                    logger.log(
                        "$count log $time",
                        when (Random.nextInt(0, 3)) {
                            0 -> Log.Level.INFO
                            1 -> Log.Level.DEBUG
                            2 -> Log.Level.ERROR
                            else -> Log.Level.INFO

                        }

                    )
                    delay(1000)

                }
            }
        }
        launchWithAppContext {
            logger.logs.collect {
                loggerView.log(it.text, it.level.mapToLogViewLevel())
            }
        }
    }

    private val loggerDockedWindow = Window("Logger", width = 150f, height = 300f, loggerView)
    private val propertyInspector = PropertyInspector()

    private val componentInspector = ComponentInspector().apply {
        val agent = Agent()
        agent.setComponent(AgentInterfaceScript())
        agent.setComponent(object: core.components.base.Component() {
            var x = 0f
            var y = 0f
            val name = "position"
            var mName = "mName"
        })
        setEntity(agent)
    }

    private val testWindow = Window(
        "Inspector", 500f, 150f, componentInspector
    )

    private val scriptViewPortWindow = Window("ScriptView", width = 500f, height = 500f, ScriptViewPort {
    })
    private val dockspace = Dockspace().apply {
        dock(loggerDockedWindow, Dockspace.Position.DOWN)
        dock(testWindow, Dockspace.Position.UP_LEFT)
        dock(scriptViewPortWindow, Dockspace.Position.UP_RIGHT)
    }
    override fun process() {
        dockspace.draw()
        loggerDockedWindow.draw()
        testWindow.draw()
        scriptViewPortWindow.draw()
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


