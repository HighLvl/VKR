import app.services.logger.Log
import app.services.logger.Logger
import core.coroutines.AppContext
import core.coroutines.launchWithAppContext
import imgui.ImGuiViewport
import imgui.app.Application
import imgui.app.Configuration
import imgui.callback.ImPlatformFuncViewport
import imgui.flag.ImGuiConfigFlags
import imgui.flag.ImGuiDir
import imgui.flag.ImGuiWindowFlags
import imgui.internal.ImGui
import imgui.internal.ImGui.dockBuilderAddNode
import imgui.internal.ImGui.dockBuilderRemoveNode
import imgui.internal.flag.ImGuiDockNodeFlags
import imgui.type.ImInt
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.net.URISyntaxException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.coroutines.ContinuationInterceptor
import kotlin.random.Random

class Main : Application() {
    private val loggerView = LoggerView()
    private var isWindowsDocked = false


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

    override fun preRun() {
        super.preRun()
        val logger = Logger()
        launchWithAppContext {
            var count = 0
            while (true) {
                count++
                logger.log(
                    "$count log",
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
        launchWithAppContext {
            logger.logs.collect {
                loggerView.log(it.text, it.level.mapToLogViewLevel())
            }

        }
    }

    private val loggerDockedWindow = Window("Logger", width = 150f, height = 300f, loggerView)
    private val testWindow = Window(
        "Logger2", 500f, 150f, object : View {
            override fun draw() {
                ImGui.plotLines("St", floatArrayOf(1f, 2f, -1f, 5f), 4)
            }
        }
    )
    private val dockspace = Dockspace().apply {
        dock(loggerDockedWindow, Dockspace.Position.DOWN)
        dock(testWindow, Dockspace.Position.UP_LEFT)
    }

    override fun process() {
        dockspace.draw()
        loggerDockedWindow.draw()
        testWindow.draw()
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
            runBlocking {
                val dispatcher = coroutineContext[ContinuationInterceptor]
                        as CoroutineDispatcher
                AppContext.context = dispatcher
            }
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

interface View {
    fun draw()
}

class Dockspace : View {
    private var id = 0
    private var docked = false

    private val dockedWindows = mutableMapOf<Position, Window>()

    override fun draw() {
        drawDockspace()
        dockWindows()
    }

    private fun drawDockspace() {
        val viewport = ImGui.getMainViewport()
        ImGui.setNextWindowSize(viewport.sizeX, viewport.sizeY)
        ImGui.setNextWindowPos(viewport.posX, viewport.posY)
        ImGui.setNextWindowViewport(viewport.id)

        val flags = ImGuiWindowFlags.NoMove or
                ImGuiWindowFlags.NoBringToFrontOnFocus or
                ImGuiWindowFlags.NoResize or
                ImGuiWindowFlags.NoScrollbar or
                ImGuiWindowFlags.NoSavedSettings or
                ImGuiWindowFlags.NoTitleBar

        if (ImGui.begin("master_window_id", flags)) {
            id = ImGui.getID("central_dockspace_id")
            ImGui.dockSpace(id)
        }
        ImGui.end()
    }

    private fun dockWindows() {
        if (docked) return
        dockBuilderRemoveNode(id)
        dockBuilderAddNode(id, ImGuiDockNodeFlags.DockSpace)
        val positionIdMap = splitDockspace()
        for( (position, window) in dockedWindows.entries) {
            val dockId = positionIdMap[position]!!
            ImGui.dockBuilderSetNodeSize(dockId, window.width, window.height)
            ImGui.dockBuilderDockWindow(window.name, dockId)
        }
        ImGui.dockBuilderFinish(id)
        docked = true
    }

    private fun splitDockspace(): Map<Position, Int> {
        val up = ImInt()
        val down = ImGui.dockBuilderSplitNode(id, ImGuiDir.Down, .25f, null, up)
        val upRight = ImInt()
        val upLeft = ImGui.dockBuilderSplitNode(up.get(), ImGuiDir.Left, .25f, null, upRight)
        return mapOf(
            Position.DOWN to down,
            Position.UP_LEFT to upLeft,
            Position.UP_RIGHT to upRight.get()
        )
    }

    fun dock(window: Window, position: Position) {
        dockedWindows[position] = window
    }

    enum class Position {
        DOWN, UP_LEFT, UP_RIGHT
    }
}

abstract class Decorator(private val view: View) : View by view

class Window(
    val name: String,
    val width: Float,
    val height: Float,
    view: View
) : Decorator(view) {

    private var isInit = false

    override fun draw() {
        if (!isInit) {
            ImGui.setNextWindowSize(width, height)
            isInit = true
        }

        if (ImGui.begin(name)) {
            super.draw()
        }
        ImGui.end()
    }
}

class LoggerView : View {
    enum class Level {
        ERROR, DEBUG, INFO
    }

    private val logs = LinkedList<Pair<String, Level>>()

    fun log(text: String, level: Level) {
        if (logs.size == MAX_LOG_LIST_SIZE) {
            logs.removeFirst()
        }
        logs.add(text to level)
    }

    override fun draw() {
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
        if (ImGui.getScrollY() == ImGui.getScrollMaxY()) {
            ImGui.setScrollHereY()
        }
        ImGui.endChild()
    }

    private fun formatLog(log: Pair<String, Level>): String {
        val (text, level) = log
        return when (level) {
            Level.ERROR -> FORMATTED_ERROR_LOG.format(text)
            Level.INFO -> FORMATTED_INFO_LOG.format(text)
            Level.DEBUG -> FORMATTED_DEBUG_LOG.format(text)
        }
    }

    companion object {
        const val MAX_LOG_LIST_SIZE = 10000

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

fun Log.Level.mapToLogViewLevel() = when (this) {
    Log.Level.INFO -> LoggerView.Level.INFO
    Log.Level.ERROR -> LoggerView.Level.ERROR
    Log.Level.DEBUG -> LoggerView.Level.DEBUG
}


