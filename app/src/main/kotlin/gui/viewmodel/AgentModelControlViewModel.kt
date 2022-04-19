package gui.viewmodel

import app.services.model.control.AgentModelControlService
import com.google.common.net.InetAddresses
import core.services.control.AgentModelControl
import core.services.control.ControlState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ButtonState(val enabled: Boolean = false, val pressed: Boolean = false, val onClickListener: () -> Unit = {})
enum class ViewControlState {
    DISCONNECT, CONNECT
}

class AgentModelControlViewModel(private val controlService: AgentModelControlService) : ViewModel() {
    private val _controlState = MutableStateFlow(ViewControlState.DISCONNECT)
    private val _ipText = MutableStateFlow(DEFAULT_IP)
    private val _port = MutableStateFlow(DEFAULT_PORT)
    private val _runButtonState = MutableStateFlow(ButtonState())
    private val _pauseButtonState = MutableStateFlow(ButtonState())
    private val _connectButtonState = MutableStateFlow(ButtonState())
    private val _disconnectButtonState = MutableStateFlow(ButtonState())
    private val _connectDataInputEnabled = MutableStateFlow(true)

    val controlState = _controlState.asStateFlow()
    val ipText = _ipText.asStateFlow()
    val port = _port.asStateFlow()
    val runButtonState = _runButtonState.asStateFlow()
    val pauseButtonState = _pauseButtonState.asStateFlow()
    val connectButtonState = _connectButtonState.asStateFlow()
    val disconnectButtonState = _disconnectButtonState.asStateFlow()
    val connectDataInputEnabled = _connectDataInputEnabled.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            controlService.controlStateFlow.collect(this@AgentModelControlViewModel::handleControlState)
        }
    }

    private fun handleControlState(state: ControlState) {
        _controlState.value = when (state) {
            ControlState.DISCONNECT -> {
                _connectDataInputEnabled.value = true
                _connectButtonState.value =
                    ButtonState(enabled = isValidIpAddress(_ipText.value), pressed = false, this::connect)
                ViewControlState.DISCONNECT
            }
            ControlState.PAUSE -> {
                _runButtonState.value = ButtonState(enabled = true, pressed = true, this::stop)
                _pauseButtonState.value = ButtonState(enabled = true, pressed = true, this::resume)
                _disconnectButtonState.value = ButtonState(enabled = true, pressed = false, this::disconnect)
                ViewControlState.CONNECT
            }
            ControlState.STOP -> {
                _runButtonState.value = ButtonState(enabled = true, pressed = false, this::run)
                _pauseButtonState.value = ButtonState(enabled = false, pressed = false)
                _disconnectButtonState.value = ButtonState(enabled = true, pressed = false, this::disconnect)
                ViewControlState.CONNECT
            }
            ControlState.RUN -> {
                _runButtonState.value = ButtonState(enabled = true, pressed = true, this::stop)
                _pauseButtonState.value = ButtonState(enabled = true, pressed = false, this::pause)
                _disconnectButtonState.value = ButtonState(enabled = true, pressed = false, this::disconnect)
                ViewControlState.CONNECT
            }
        }
    }

    private fun connect() {
        _connectButtonState.value = ButtonState(enabled = true, pressed = true, this::disconnect)
        launchWithAppContext {
            controlService.connect(_ipText.value, _port.value)
        }
    }

    private fun run() {
        _runButtonState.value = ButtonState(enabled = false, pressed = true)
        launchWithAppContext {
            controlService.runModel()
        }
    }

    private fun resume() {
        _pauseButtonState.value = ButtonState(enabled = false, pressed = false)
        _runButtonState.value = ButtonState(enabled = false, pressed = true)
        launchWithAppContext {
            controlService.resumeModel()
        }
    }

    private fun pause() {
        _pauseButtonState.value = ButtonState(enabled = false, pressed = true)
        _runButtonState.value = ButtonState(enabled = false, pressed = true)
        launchWithAppContext {
            controlService.pauseModel()
        }
    }

    private fun stop() {
        _pauseButtonState.value = ButtonState(enabled = false, pressed = false)
        _runButtonState.value = ButtonState(enabled = false, pressed = false)
        launchWithAppContext {
            controlService.stopModel()
        }
    }

    private fun disconnect() {
        launchWithAppContext {
            controlService.disconnect()
            viewModelScope.coroutineContext.cancelChildren()
        }
    }

    fun changeRequestPeriod(periodSec: Float) {
        launchWithAppContext {
            controlService.changeRequestPeriod(periodSec)
        }
    }

    fun validateIp(ip: String) {
        if (!isValidIpAddress(ip)) {
            _connectButtonState.value = ButtonState(false)
            return
        }
        _connectButtonState.value = ButtonState(true)
    }

    fun validatePort(port: Int) {
        _port.value = port.coerceIn(1024, 49151)
    }

    private fun isValidIpAddress(ip: String) = InetAddresses.isInetAddress(ip)

    private companion object {
        const val DEFAULT_IP = "127.0.0.1"
        const val DEFAULT_PORT = 1024
    }
}