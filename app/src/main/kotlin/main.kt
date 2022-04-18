import gui.controllers.MainController
import imgui.ImFontConfig
import imgui.ImGuiIO
import imgui.ImGuiViewport
import imgui.app.Application
import imgui.app.Configuration
import imgui.callback.ImPlatformFuncViewport
import imgui.extension.implot.ImPlot
import imgui.extension.implot.ImPlotContext
import imgui.flag.ImGuiConfigFlags
import imgui.internal.ImGui
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.lwjgl.glfw.GLFW
import gui.utils.Contexts
import java.io.IOException
import java.net.URISyntaxException
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime

class Main : Application() {
    private lateinit var imPlotContext: ImPlotContext
    private val mainController = MainController()

    override fun configure(config: Configuration) {
        config.title = ""
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
        initFonts(io)
        imPlotContext = ImPlot.createContext()
    }

    private fun initFonts(io: ImGuiIO) {
        val fontConfig = ImFontConfig()
        val ranges = shortArrayOf(0x0020, 0x00FF, 0x0400, 0x044F, 0)
        io.fonts.addFontFromMemoryTTF(
            loadFromResources("roboto-regular.ttf"),
            14f * SCALE_FACTOR,
            fontConfig,
            ranges
        )
        io.fonts.build()
        fontConfig.destroy()
    }

    override fun dispose() {
        super.dispose()
        ImPlot.destroyContext(imPlotContext)
    }

    @OptIn(ExperimentalTime::class)
    override fun preRun() {
        super.preRun()
        mainController.onPreRun()
    }

    override fun postRun() {
        super.postRun()
        mainController.stop()
    }

    override fun process() {
        mainController.update()
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
            Contexts.ui = currentCoroutineContext()
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

