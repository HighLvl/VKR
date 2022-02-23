package views

import com.google.common.net.InetAddresses
import imgui.flag.ImGuiInputTextFlags
import imgui.flag.ImGuiStyleVar
import imgui.internal.ImGui
import imgui.internal.flag.ImGuiItemFlags
import imgui.type.ImFloat
import imgui.type.ImInt
import imgui.type.ImString

class ModelControlView : View {
    var onClickConnectListener: (String, Int) -> Unit = { _, _ -> }
    var onClickStopListener: () -> Unit = {}
    var onClickRunListener: () -> Unit = {}
    var onClickPauseListener: () -> Unit = {}
    var onClickResumeListener: () -> Unit = {}
    var onClickDisconnectListener: () -> Unit = {}
    var onChangeDtListener: (Float) -> Unit = {}

    private var ipText = ImString(DEFAULT_IP, 15)
    private val port = ImInt(1024)

    var width = 0f
        private set

    private enum class State {
        CONNECT, RUN, PAUSE, STOP
    }

    private var state = State.CONNECT

    private val connectButton = Button(TITLE_CONNECT_BUTTON).apply {
        enabled = isValidIpAddress()
        onClickListener = {
            onClickConnectListener(ipText.get(), port.get())
            onChangeDtListener(inputRequestDt.value)
        }
    }

    private val runButton = Button(TITLE_RUN_BUTTON).apply { bindKey(Key.R) }
    private val pauseButton = Button(TITLE_PAUSE_BUTTON).apply { bindKey(Key.P) }
    private val disconnectButton = Button(TITLE_DISCONNECT_BUTTON)
    private val inputRequestDt = InputRequestDt().apply {
        onChangeDtListener = { this@ModelControlView.onChangeDtListener(it) }
    }

    override fun draw() {
        when (state) {
            State.CONNECT -> handleConnectState()
            State.RUN -> handleRunState()
            State.PAUSE -> handlePauseState()
            State.STOP -> handleStopState()
        }
        width = when (state) {
            State.CONNECT -> {
                WIDTH_IP + WIDTH_PORT + WIDTH_CONNECT_BUTTON
            }
            else -> {
                WIDTH_RUN_BUTTON +
                        WIDTH_PAUSE_BUTTON +
                        WIDTH_DISCONNECT_BUTTON +
                        WIDTH_INPUT_REQUEST_DT +
                        ImGui.getStyle().itemSpacingX * 8
            }
        }
    }

    private fun handleConnectState() {
        disconnectButton.onClickListener = onClickDisconnectListener

        ImGui.setNextItemWidth(WIDTH_IP)
        if (ImGui.inputText(LABEL_INPUT_IP, ipText)) {
            connectButton.enabled = isValidIpAddress()
        }
        ImGui.sameLine()
        ImGui.setNextItemWidth(WIDTH_PORT)
        if (ImGui.inputInt(LABEL_INPUT_PORT, port)) {
            port.set(port.get().coerceIn(1024, 49151))
        }
        ImGui.sameLine()

        connectButton.draw()
    }

    private fun isValidIpAddress(): Boolean {
        return InetAddresses.isInetAddress(ipText.get())
    }

    private fun handleRunState() {
        runButton.apply {
            pressed = true
            enabled = true
            onClickListener = onClickStopListener
        }
        pauseButton.apply {
            enabled = true
            pressed = false
            onClickListener = onClickPauseListener
        }
        drawControlViews()
    }

    private fun handlePauseState() {
        runButton.apply {
            pressed = true
            enabled = true
            onClickListener = onClickStopListener
        }
        pauseButton.apply {
            enabled = true
            pressed = true
            onClickListener = onClickResumeListener
        }
        drawControlViews()
    }

    private fun handleStopState() {
        runButton.apply {
            pressed = false
            enabled = true
            onClickListener = { onClickRunListener() }
        }
        pauseButton.apply {
            pressed = false
            enabled = false
            onClickListener = {}
        }
        drawControlViews()
    }


    private fun drawControlViews() {
        inputRequestDt.draw()
        repeat(4) {
            ImGui.sameLine()
            ImGui.spacing()
        }
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, SPACING_BETWEEN_PLAY_AND_PAUSE, ImGui.getStyle().itemSpacing.y)
        ImGui.sameLine()
        runButton.draw()
        ImGui.sameLine()
        pauseButton.draw()
        ImGui.popStyleVar()
        repeat(4) {
            ImGui.sameLine()
            ImGui.spacing()
        }
        ImGui.sameLine()
        disconnectButton.draw()
    }

    fun connect() {
        state = State.STOP
    }

    fun stop() {
        state = State.STOP
    }

    fun pause() {
        state = State.PAUSE
    }

    fun run() {
        state = State.RUN
    }

    fun disconnect() {
        state = State.CONNECT
    }

    private class InputRequestDt : View {
        var enabled = true
        private val imDt = ImFloat(REQUEST_DT_VALUE_MIN)
        var onChangeDtListener: (Float) -> Unit = {}

        val value: Float
            get() = imDt.get()

        override fun draw() {
            if (!enabled) {
                ImGui.pushItemFlag(ImGuiItemFlags.Disabled, true)
                ImGui.pushStyleVar(ImGuiStyleVar.Alpha, ImGui.getStyle().alpha * 0.5f)
            }
            ImGui.pushItemWidth(WIDTH_INPUT_REQUEST_DT)
            if (ImGui.inputFloat(
                    LABEL_INPUT_REQUEST_DT,
                    imDt,
                    0f,
                    0f,
                    "%g",
                    ImGuiInputTextFlags.EnterReturnsTrue
                )
            ) {
                imDt.set(imDt.get().coerceIn(REQUEST_DT_VALUE_MIN, REQUEST_DT_VALUE_MAX))
                onChangeDtListener(imDt.get())
            }
            if (!enabled) {
                ImGui.popItemFlag()
                imgui.ImGui.popStyleVar()
            }
        }
    }

    private companion object {
        const val WIDTH_IP = 200f
        const val WIDTH_PORT = 200f
        const val SPACING_BETWEEN_PLAY_AND_PAUSE = 2f
        const val TITLE_CONNECT_BUTTON = "Connect"
        const val TITLE_RUN_BUTTON = "Run"
        const val TITLE_PAUSE_BUTTON = "Pause"
        const val TITLE_DISCONNECT_BUTTON = "Disconnect"

        const val DEFAULT_IP = "127.0.0.1"
        const val LABEL_INPUT_IP = "ip"
        const val LABEL_INPUT_PORT = "port"

        const val WIDTH_DISCONNECT_BUTTON = 100f
        const val WIDTH_CONNECT_BUTTON = 100f
        const val WIDTH_RUN_BUTTON = 100f
        const val WIDTH_PAUSE_BUTTON = 100f
        const val LABEL_INPUT_REQUEST_DT = "dt, s"
        const val WIDTH_INPUT_REQUEST_DT = 100f
        const val REQUEST_DT_VALUE_MIN = 0.001f
        const val REQUEST_DT_VALUE_MAX = 60f
    }
}