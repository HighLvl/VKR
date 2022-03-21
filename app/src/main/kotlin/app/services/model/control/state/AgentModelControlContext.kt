package app.services.model.control.state

import app.api.base.AgentModelApi
import app.api.dto.InputData
import app.api.dto.ModelInputArgs
import app.api.dto.Request
import app.api.dto.Snapshot
import app.coroutines.Contexts
import app.requests.RequestSender
import app.services.model.control.PeriodTaskExecutor
import app.services.scene.SceneApi
import core.services.control.ControlState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow

class AgentModelControlContext(
    val modelApi: AgentModelApi,
    private val sceneApi: SceneApi,
    private val requestSender: RequestSender
) {
    val controlState: MutableSharedFlow<ControlState> = MutableSharedFlow(replay = 1)
    val periodTaskExecutor = PeriodTaskExecutor(Contexts.app)
    private val inputArgs: ModelInputArgs
        get() = sceneApi.getInputArgs()

    private var periodSec: Float = 0.1f
    private lateinit var currentState: State
    private val coroutineScope = CoroutineScope(Contexts.app)

    fun start() {
        setState(DisconnectState)
    }

    fun stop() {
        disconnect()
        coroutineScope.coroutineContext.cancelChildren()
    }

    fun sendControlRequest(controlRequest: ControlRequest) {
        when (controlRequest) {
            ControlRequest.RESUME -> {
                requestSender.sendRequest<Unit>(0, "Resume", listOf()) {
                    it.onSuccess { onResume() }
                }
            }
            ControlRequest.PAUSE -> {
                requestSender.sendRequest<Unit>(0, "Pause", listOf()) {
                    it.onSuccess { onPause() }
                }
            }
            ControlRequest.RUN -> {
                requestSender.sendRequest<Unit>(0, "Run", inputArgs.args.values.toList()) {
                    it.onSuccess { onRun() }
                }
            }
            ControlRequest.STOP -> {
                requestSender.sendRequest<Unit>(0, "Stop", listOf()) {
                    it.onSuccess { onStop() }
                }
            }
        }
    }

    suspend fun connect(ip: String, port: Int) {
        currentState.connect(this, ip, port)
    }

    fun disconnect() {
        periodTaskExecutor.stop()
        modelApi.disconnect()
        setState(DisconnectState)
    }

    private var startTime: Long = 0
    fun runModel() {
        startTime = System.currentTimeMillis()
        currentState.run(this)
    }

    fun pauseModel() {
        currentState.pause(this)
    }

    fun resumeModel() {
        currentState.resume(this)
    }

    fun stopModel() {
        currentState.stop(this)
        println(System.currentTimeMillis() - startTime)
    }

    fun setState(state: State) {
        currentState = state
        coroutineScope.launch {
            withContext(Dispatchers.IO) { controlState.emit(getControlState()) }
        }
    }

    private fun getControlState(): ControlState = when (currentState::class) {
        DisconnectState::class -> ControlState.DISCONNECT
        StopState::class -> ControlState.STOP
        RunState::class -> ControlState.RUN
        PauseState::class -> ControlState.PAUSE
        else -> throw IllegalStateException()
    }

    fun changeRequestPeriod(periodSec: Float) {
        if (periodSec < 0) throw IllegalArgumentException("Period must be more than zero")
        periodTaskExecutor.changePeriod(periodSec)
    }

    private fun onPause() = sceneApi.onModelPause()
    private fun onRun() = sceneApi.onModelRun()
    private fun onResume() = sceneApi.onModelResume()
    private fun onStop() = sceneApi.onModelStop()
    fun onUpdate(snapshot: Snapshot) = sceneApi.updateWith(snapshot)
    fun getInputData(): InputData = InputData(sceneApi.getRequests())
    fun onConnect(state: app.api.dto.State) {
        when(state) {
            app.api.dto.State.RUN -> onRun()
            app.api.dto.State.PAUSE -> onPause()
            app.api.dto.State.STOP -> onStop()
        }
    }

    enum class ControlRequest {
        RUN, PAUSE, STOP, RESUME
    }
}