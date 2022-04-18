package gui.view

import imgui.flag.ImGuiInputTextFlags
import imgui.flag.ImGuiStyleVar
import imgui.internal.ImGui
import imgui.internal.flag.ImGuiItemFlags
import imgui.type.ImFloat
import gui.utils.getString
import gui.viewmodel.AgentModelControlViewModel
import gui.viewmodel.ButtonState
import gui.viewmodel.ViewControlState
import gui.widgets.Button
import gui.widgets.Key
import gui.widgets.Widget
import gui.widgets.input.InputInt
import gui.widgets.input.InputString

class ModelControlView(private val viewModel: AgentModelControlViewModel) : View(), Widget {
    var width = 0f
        private set

    private val connectButton = Button(TITLE_CONNECT_BUTTON)
    private val runButton = Button(TITLE_RUN_BUTTON).apply { bindKey(Key.R) }
    private val pauseButton = Button(TITLE_PAUSE_BUTTON).apply { bindKey(Key.P) }
    private val disconnectButton = Button(TITLE_DISCONNECT_BUTTON)
    private val inputRequestDt = InputRequestDt()
    private val ipInputString = InputString("", LABEL_INPUT_IP, 15)
    private val portInputInt = InputInt(0, LABEL_INPUT_PORT)

    private var state = ViewControlState.DISCONNECT

    override fun onPreRun() {
        viewModel.ipText.collectWithUiContext {
            ipInputString.value = it
        }

        viewModel.port.collectWithUiContext {
            portInputInt.value = it
        }

        viewModel.controlState.collectWithUiContext {
            state = it
        }

        viewModel.connectDataInputEnabled.collectWithUiContext {
            ipInputString.enabled = it
            portInputInt.enabled = it
        }

        viewModel.connectButtonState.collectWithUiContext {
            connectButton.bindButtonState(it)
        }

        viewModel.disconnectButtonState.collectWithUiContext {
            disconnectButton.bindButtonState(it)
        }

        viewModel.runButtonState.collectWithUiContext {
            runButton.bindButtonState(it)
        }

        viewModel.pauseButtonState.collectWithUiContext {
            pauseButton.bindButtonState(it)
        }
        inputRequestDt.onChangeDtListener = viewModel::changeRequestPeriod
        portInputInt.onChangeValueListener = viewModel::validatePort
        ipInputString.onChangeValueListener = viewModel::validateIp
    }

    private fun Button.bindButtonState(buttonState: ButtonState) {
        enabled = buttonState.enabled
        pressed = buttonState.pressed
        onClickListener = buttonState.onClickListener
    }

    override fun draw() {
        when (state) {
            ViewControlState.CONNECT -> handleConnectState()
            ViewControlState.DISCONNECT -> handleDisconnectState()
        }
    }

    private fun handleDisconnectState() {
        width = WIDTH_IP +
                WIDTH_PORT +
                WIDTH_CONNECT_BUTTON +
                WIDTH_INPUT_REQUEST_DT +
                ImGui.getStyle().itemSpacingX * 4
        inputRequestDt.draw()
        repeat(4) {
            ImGui.sameLine()
            ImGui.spacing()
        }
        ImGui.sameLine()
        ImGui.setNextItemWidth(WIDTH_IP)
        ipInputString.draw()
        ImGui.sameLine()
        ImGui.setNextItemWidth(WIDTH_PORT)
        portInputInt.draw()
        ImGui.sameLine()
        connectButton.draw()
    }

    private fun handleConnectState() {
        width = WIDTH_RUN_BUTTON +
                WIDTH_PAUSE_BUTTON +
                WIDTH_DISCONNECT_BUTTON +
                WIDTH_INPUT_REQUEST_DT +
                ImGui.getStyle().itemSpacingX * 8
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

    private class InputRequestDt : Widget {
        var enabled = true
        private val imDt = ImFloat(REQUEST_DT_VALUE_DEFAULT)
        var onChangeDtListener: (Float) -> Unit = {}
            set(value) {
                field = value
                value(imDt.get())
            }

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
        val TITLE_CONNECT_BUTTON = getString("connect_button_title")
        val TITLE_RUN_BUTTON = getString("run_button_title")
        val TITLE_PAUSE_BUTTON = getString("pause_button_title")
        val TITLE_DISCONNECT_BUTTON = getString("disconnect_button_title")

        val LABEL_INPUT_IP = getString("input_ip_label")
        val LABEL_INPUT_PORT = getString("input_port_label")

        const val WIDTH_DISCONNECT_BUTTON = 100f
        const val WIDTH_CONNECT_BUTTON = 100f
        const val WIDTH_RUN_BUTTON = 100f
        const val WIDTH_PAUSE_BUTTON = 100f
        val LABEL_INPUT_REQUEST_DT = getString("input_request_dt_label")
        const val WIDTH_INPUT_REQUEST_DT = 100f

        const val REQUEST_DT_VALUE_DEFAULT = 0.001f
        const val REQUEST_DT_VALUE_MAX = 60f
        const val REQUEST_DT_VALUE_MIN = 0.0001f
    }
}