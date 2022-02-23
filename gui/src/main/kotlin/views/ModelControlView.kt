package views

import com.google.common.net.InetAddresses
import imgui.flag.ImGuiInputTextFlags
import imgui.flag.ImGuiStyleVar
import imgui.internal.ImGui
import imgui.internal.flag.ImGuiItemFlags
import imgui.type.ImFloat
import views.input.InputInt
import views.input.InputString

class ModelControlView : View {
    var onClickConnectListener: (String, Int) -> Unit = { _, _ -> }
    var onClickStopListener: () -> Unit = {}
    var onClickRunListener: () -> Unit = {}
    var onClickPauseListener: () -> Unit = {}
    var onClickResumeListener: () -> Unit = {}
    var onClickDisconnectListener: () -> Unit = {}
    var onChangeDtListener: (Float) -> Unit = {}
    var width = 0f
        private set
    private var ipText = DEFAULT_IP
    private var port = 1024

    private enum class State {
        CONNECT, RUN, PAUSE, STOP
    }

    private var state = State.CONNECT
    private val connectButton = Button(TITLE_CONNECT_BUTTON)
    private val runButton = Button(TITLE_RUN_BUTTON).apply { bindKey(Key.R) }
    private val pauseButton = Button(TITLE_PAUSE_BUTTON).apply { bindKey(Key.P) }
    private val disconnectButton = Button(TITLE_DISCONNECT_BUTTON)
    private val inputRequestDt = InputRequestDt().apply {
        onChangeDtListener = { this@ModelControlView.onChangeDtListener(it) }
    }
    private val ipInputString = InputString(ipText, LABEL_INPUT_IP, 15)
    private val portInputInt = InputInt(port, LABEL_INPUT_PORT)

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
        ImGui.setNextItemWidth(WIDTH_IP)
        ipInputString.draw()
        ImGui.sameLine()
        ImGui.setNextItemWidth(WIDTH_PORT)
        portInputInt.draw()
        ImGui.sameLine()

        connectButton.draw()
    }

    private fun isValidIpAddress(): Boolean {
        return InetAddresses.isInetAddress(ipText)
    }

    private fun handleRunState() {
        drawControlViews()
    }

    private fun handlePauseState() {
        drawControlViews()
    }

    private fun handleStopState() {
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
        disconnectButton.onClickListener = { onClickDisconnectListener() }
        stop()
    }

    fun stop() {
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
        inputRequestDt.enabled = true
        initDisconnectButton()
        state = State.STOP
    }

    private fun initDisconnectButton() {
        disconnectButton.apply {
            pressed = false
            enabled = true
            onClickListener = { onClickDisconnectListener() }
        }
    }

    fun pause() {
        runButton.apply {
            pressed = true
            enabled = true
            onClickListener = { onClickStopListener() }
        }
        pauseButton.apply {
            enabled = true
            pressed = true
            onClickListener = { onClickResumeListener() }
        }
        inputRequestDt.enabled = true
        initDisconnectButton()
        state = State.PAUSE
    }

    fun run() {
        runButton.apply {
            pressed = true
            enabled = true
            onClickListener = {
                onClickStopListener()
            }
        }
        pauseButton.apply {
            enabled = true
            pressed = false
            onClickListener = { onClickPauseListener() }
        }
        inputRequestDt.enabled = true
        initDisconnectButton()
        state = State.RUN
    }

    fun disconnect() {
        connectButton.apply {
            enabled = isValidIpAddress()
            pressed = false
            onClickListener = { onClickConnectListener(ipText, port) }
        }
        inputRequestDt.enabled = true
        ipInputString.apply {
            enabled = true
            onChangeValueListener = {
                ipText = it
                connectButton.enabled = isValidIpAddress()
            }
        }
        portInputInt.apply {
            enabled = true
            onChangeValueListener = {
                value = it.coerceIn(1024, 49151)
                port = value
            }
        }
        state = State.CONNECT
    }

    fun disableAll() {
        runButton.enabled = false
        disconnectButton.enabled = false
        pauseButton.enabled = false
        connectButton.enabled = false
        inputRequestDt.enabled = false
        ipInputString.enabled = false
        portInputInt.enabled = false
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