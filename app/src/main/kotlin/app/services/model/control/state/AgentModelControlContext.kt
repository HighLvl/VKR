package app.services.model.control.state

import app.api.base.AgentModelApi
import app.api.dto.Error
import app.api.dto.Requests
import app.api.dto.ModelInputArgs
import app.api.dto.Responses
import app.api.dto.Snapshot
import app.coroutines.Contexts
import app.requests.RequestDispatcher
import app.requests.RequestSender
import app.requests.sendRequest
import app.services.model.control.PeriodTaskExecutor
import app.services.scene.SceneApi
import core.services.control.ControlState

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow

class AgentModelControlContext(
    val modelApi: AgentModelApi,
    private val sceneApi: SceneApi,
    private val requestDispatcher: RequestDispatcher,
    private val requestSender: RequestSender
) {
    val controlState: MutableSharedFlow<ControlState> = MutableSharedFlow(replay = 1)
    val periodTaskExecutor = PeriodTaskExecutor(Contexts.app)
    private val inputArgs: ModelInputArgs
        get() = sceneApi.getInputArgs()

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
                    it.onSuccess { onResume() }.onFailure { emitCurrentState() }
                }
            }
            ControlRequest.PAUSE -> {
                requestSender.sendRequest<Unit>(0, "Pause", listOf()) {
                    it.onSuccess { onPause() }.onFailure { emitCurrentState() }
                }
            }
            ControlRequest.RUN -> {
                requestSender.sendRequest<Unit>(0, "Run", inputArgs.args.values.toList()) {
                    it.onSuccess { onRun() }.onFailure { emitCurrentState() }
                }
            }
            ControlRequest.STOP -> {
                requestSender.sendRequest<Unit>(0, "Stop", listOf()) {
                    it.onSuccess { onStop() }.onFailure { emitCurrentState() }
                }
            }
            ControlRequest.GET_STATE -> {
                requestSender.sendRequest<app.api.dto.State>(0, "GetState", listOf()) { result ->
                    result.onSuccess { onConnect(it) }.onFailure { emitCurrentState() }
                }
            }
            ControlRequest.GET_SNAPSHOT -> {
                requestSender.sendRequest<Snapshot>(0, "GetSnapshot", listOf()) { result ->
                    result.onSuccess { onUpdate(it) }
                }
            }
        }
    }

    fun handleResponses(responses: Responses) {
        requestDispatcher.handleResponses(responses)
    }

    fun commitRequests(): Requests = requestDispatcher.commitRequests()


    suspend fun connect(ip: String, port: Int) {
        currentState.connect(this, ip, port)
    }

    fun disconnect() {
        periodTaskExecutor.stop()
        modelApi.disconnect()
        requestDispatcher.clear()
        setState(DisconnectState)
        sceneApi.onModelStop()
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
        emitCurrentState()
    }

    private fun emitCurrentState() {
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

    private fun onPause() {
        periodTaskExecutor.pause()
        setState(PauseState)
        sceneApi.onModelPause()
    }

    private fun onRun() {
        setState(RunState)
        sceneApi.onModelRun()
    }

    private fun onResume() {
        setState(RunState)
        sceneApi.onModelResume()
    }

    private fun onStop() {
        periodTaskExecutor.stop()
        setState(StopState)
        sceneApi.onModelStop()
    }
    private fun onUpdate(snapshot: Snapshot) {
        sceneApi.updateWith(snapshot)
    }

    private fun onConnect(state: app.api.dto.State) {
        when (state) {
            app.api.dto.State.RUN -> onRun()
            app.api.dto.State.PAUSE -> onPause()
            app.api.dto.State.STOP -> onStop()
        }
    }

    fun update() {
        currentState.update(this)
    }

    enum class ControlRequest {
        RUN, PAUSE, STOP, RESUME, GET_STATE, GET_SNAPSHOT
    }
}