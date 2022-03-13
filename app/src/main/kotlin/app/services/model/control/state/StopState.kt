package app.services.model.control.state

import core.services.logger.Level
import core.services.logger.Logger
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object StopState : ConnectState() {
    private var runJob: Job? = null

    override fun run(context: AgentModelControlContext, periodSec: Float) {
        runJob?.let { return }
        runJob = context.coroutineScope.launch {
            runModel(context)
            runJob = null
        }
    }

    private suspend fun runModel(context: AgentModelControlContext) {
        try {
            context.modelApi.run(context.globalArgs)
            context.periodTaskExecutor.scheduleTask {
                updateAgentModel(context)
            }
            context.onStart()
            context.setState(RunState)
        } catch (e: Exception) {
            disconnect(context)
            Logger.log(e.message.orEmpty(), Level.ERROR)
        }
    }

    private suspend fun updateAgentModel(context: AgentModelControlContext) {
        try {
            val behaviour = context.getBehaviour()
            if (behaviour.requests.isNotEmpty())
                context.modelApi.callBehaviourFunctions(behaviour)
            val snapshot = context.modelApi.requestSnapshot()
            context.onUpdate(snapshot)
        } catch (e: Exception) {
            disconnect(context)
            Logger.log(e.message.orEmpty(), Level.ERROR)
        }
    }

    fun restoreRun(context: AgentModelControlContext) {
        context.periodTaskExecutor.scheduleTask {
            updateAgentModel(context)
        }
        context.onStart()
        context.setState(RunState)
    }

    fun restorePause(context: AgentModelControlContext) {
        context.periodTaskExecutor.pause()
        context.periodTaskExecutor.scheduleTask {
            updateAgentModel(context)
        }
        context.onStart()
        context.onPause()
        context.setState(PauseState)
    }

    override fun disconnect(context: AgentModelControlContext) {
        runJob?.cancel()
        super.disconnect(context)
    }
}