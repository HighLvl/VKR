package app.services.model.control.state

import io.reactivex.rxjava3.subjects.PublishSubject
import core.services.*
import core.api.AgentModelApiClient
import core.api.dto.GlobalArgs
import core.api.dto.Snapshot
import core.coroutines.PeriodTaskExecutor
import app.services.model.control.ControlAction

class AgentModelControlContext(val apiClient: AgentModelApiClient) {
    private var state: State = StopState
    val periodTaskExecutor = PeriodTaskExecutor()

    val availableControlActions: PublishSubject<List<ControlAction>> =
        PublishSubject.create<List<ControlAction>>().apply { onNext(listOf()) }
    var globalArgs = GlobalArgs(mutableMapOf())

    fun start() {
        subscribeOnGlobalArgs()
    }

    private fun subscribeOnGlobalArgs() {
        EventBus.listen<AgentModelLifecycleEvent.GlobalArgsSet>().subscribe() {
            globalArgs = it.args
        }
    }

    fun run(periodSec: Float) {
        state.run(this, periodSec)
    }

    fun pause() {
        state.pause(this)
    }

    fun resume() {
        state.resume(this)
    }

    fun stop() {
        state.stop(this)
    }

    fun setState(state: State) {
        this.state = state
        state.onThisStateChanged(this)
    }

    fun onPause() {
        EventBus.publish(AgentModelLifecycleEvent.Pause)
    }

    fun onStart() {
        EventBus.publish(AgentModelLifecycleEvent.Start)
    }

    fun onResume() {
        EventBus.publish(AgentModelLifecycleEvent.Resume)
    }

    fun onStop() {
        EventBus.publish(AgentModelLifecycleEvent.Stop)
    }

    fun onUpdate(snapshot: Snapshot) {
        EventBus.publish(SnapshotReceive(snapshot))
    }
}