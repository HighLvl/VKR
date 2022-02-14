import imgui.*
import imgui.app.Application
import imgui.app.Configuration
import imgui.callback.ImPlatformFuncViewport
import imgui.flag.ImGuiConfigFlags
import imgui.flag.ImGuiWindowFlags
import java.io.IOException
import java.net.URISyntaxException
import java.nio.file.Files
import java.nio.file.Paths

class Main : Application() {
    private val logger = Logger()


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

    private var dockspaceId: Int = 0

    override fun process() {
        val viewport =ImGui.getMainViewport()
        ImGui.setNextWindowSize(viewport.sizeX, viewport.sizeY)
        ImGui.setNextWindowPos(viewport.posX, viewport.posY)
        ImGui.setNextWindowViewport(viewport.id)

        val flags = ImGuiWindowFlags.NoMove or
                ImGuiWindowFlags.NoBringToFrontOnFocus or
                ImGuiWindowFlags.NoResize or
                ImGuiWindowFlags.NoScrollbar or
                ImGuiWindowFlags.NoSavedSettings or
                ImGuiWindowFlags.NoTitleBar

        if (ImGui.begin("master_window_id",flags)) {
            ImGui.getPlatformIO().setPlatformOnChangedViewport(OnChangedViewportListener)
            dockspaceId = ImGui.getID("central_dockspace_id")
            ImGui.dockSpace(dockspaceId)
        }
        ImGui.end()

        if (ImGui.begin("Logger")) {
            logger.draw()
        }
        ImGui.end()

        if (ImGui.begin("Logger2")) {
            ImGui.plotLines("St", floatArrayOf(1f, 2f, -1f, 5f), 4)
        }
        ImGui.end()
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


    companion object {
        const val SCALE_FACTOR = 2f

        @JvmStatic
        fun main(args: Array<String>) {
            launch(Main())
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

class Logger {
    enum class Level {
        ERROR, DEBUG, INFO
    }

    private val logs = mutableListOf<Pair<String, Level>>()

    fun log(text: String, level: Level) {
        logs.add(text to level)
    }

    fun draw() {
        if (ImGui.smallButton("Debug")) {
            log("Some debug", Level.DEBUG)
        }
        ImGui.sameLine()
        if (ImGui.smallButton("Error")) {
            log("Some error", Level.ERROR)
        }
        ImGui.sameLine()
        if (ImGui.smallButton("Info")) {
            log("Some info", Level.INFO)
        }
        ImGui.sameLine()
        if (ImGui.smallButton(TITLE_CLEAR_BUTTON)) {
            logs.clear()
        }
        ImGui.separator()

        ImGui.beginChild(ID_LOG_CONTAINER)
        for (log in logs) {
            val formattedLog = formatLog(log)
            val color = when (log.second) {
                Level.ERROR -> ERROR_COLOR
                Level.DEBUG -> DEBUG_COLOR
                Level.INFO -> INFO_COLOR
            }
            ImGui.textColored(color[0], color[1], color[2], color[3], formattedLog)
        }
        ImGui.endChild()
    }

    private fun formatLog(log: Pair<String, Level>): String {
        val (text, level) = log
        return when(level) {
            Level.ERROR -> FORMATTED_ERROR_LOG.format(text)
            Level.INFO -> FORMATTED_INFO_LOG.format(text)
            Level.DEBUG -> FORMATTED_DEBUG_LOG.format(text)
        }
    }

    companion object {
        const val FORMATTED_ERROR_LOG = "[error] %s"
        const val FORMATTED_INFO_LOG = "[info] %s"
        const val FORMATTED_DEBUG_LOG = "[debug] %s"
        const val TITLE_CLEAR_BUTTON = "Clear"


        val ERROR_COLOR = listOf(255, 100, 100, 255)
        val INFO_COLOR = listOf(100, 100, 255, 255)
        val DEBUG_COLOR = listOf(10, 150, 10, 255)

        const val ID_LOG_CONTAINER = "log container"
    }
}

