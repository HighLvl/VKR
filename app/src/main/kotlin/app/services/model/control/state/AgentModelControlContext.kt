package app.services.model.control.state

import app.api.base.AgentModelApi
import app.api.dto.ModelInputArgs
import app.api.dto.Requests
import app.api.dto.Responses
import app.api.dto.Snapshot
import core.coroutines.Contexts
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
    val controlState: ControlState
        get() = mapStateToControlState()
    val controlStateFlow: MutableSharedFlow<ControlState> = MutableSharedFlow(replay = 1)
    val periodTaskExecutor = PeriodTaskExecutor(Contexts.app)
    private val inputArgs: ModelInputArgs
        get() = sceneApi.getInputArgs()

    private lateinit var currentState: State
    private val coroutineScope = CoroutineScope(Contexts.app)

    fun start() {
        setState(DisconnectState)
    }

    fun stop() {
        runBlocking { disconnect() }
        coroutineScope.coroutineContext.cancelChildren()
    }

    fun sendControlRequest(controlRequest: ControlRequest, onResult: suspend (Result<Unit>) -> Unit = {}) {
        when (controlRequest) {
            ControlRequest.RESUME -> {
                requestSender.sendRequest<Unit>(0, "Resume", listOf()) {
                    it.onSuccess { onResume() }.onFailure { emitCurrentState() }
                    onResult(it)
                }
            }
            ControlRequest.PAUSE -> {
                requestSender.sendRequest<Unit>(0, "Pause", listOf()) {
                    it.onSuccess { onPause() }.onFailure { emitCurrentState() }
                    onResult(it)
                }
            }
            ControlRequest.RUN -> {
                requestSender.sendRequest<Unit>(0, "Run", inputArgs.args.values.toList()) {
                    it.onSuccess { onRun() }.onFailure { emitCurrentState() }
                    onResult(it)
                }
            }
            ControlRequest.STOP -> {
                requestSender.sendRequest<Unit>(0, "Stop", listOf()) {
                    it.onSuccess { onStop() }.onFailure { emitCurrentState() }
                    onResult(it)
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

    suspend fun handleResponses(responses: Responses) {
        requestDispatcher.handleResponses(responses)
    }

    fun commitRequests(): Requests = requestDispatcher.commitRequests()


    suspend fun connect(ip: String, port: Int) {
        currentState.connect(this, ip, port)
    }

    suspend fun disconnect() {
        periodTaskExecutor.stop()
        modelApi.disconnect()
        requestDispatcher.clear()
        setState(DisconnectState)
        sceneApi.onModelStop()
    }

    private var startTime: Long = 0
    fun runModel(onResult: (Result<Unit>) -> Unit) {
        startTime = System.currentTimeMillis()
        currentState.run(this, onResult)
    }

    fun pauseModel(onResult: (Result<Unit>) -> Unit) {
        currentState.pause(this, onResult)
    }

    fun resumeModel(onResult: (Result<Unit>) -> Unit) {
        currentState.resume(this, onResult)
    }

    fun stopModel(onResult: (Result<Unit>) -> Unit) {
        currentState.stop(this, onResult)
    }

    fun setState(state: State) {
        currentState = state
        emitCurrentState()
    }

    private fun emitCurrentState() {
        coroutineScope.launch {
            withContext(Dispatchers.IO) { controlStateFlow.emit(mapStateToControlState()) }
        }
    }

    private fun mapStateToControlState(): ControlState = when (currentState::class) {
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

    private suspend fun onPause() {
        periodTaskExecutor.pause()
        setState(PauseState)
        sceneApi.onModelPause()
    }

    private suspend fun onRun() {
        setState(RunState)
        sceneApi.onModelRun()
    }

    private suspend fun onResume() {
        setState(RunState)
        sceneApi.onModelResume()
    }

    private suspend fun onStop() {
        periodTaskExecutor.stop()
        setState(StopState)
        sceneApi.onModelStop()
    }

    private suspend fun onUpdate(snapshot: Snapshot) {
        sceneApi.updateWith(snapshot)
    }

    private suspend fun onConnect(state: app.api.dto.State) {
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