package app.services.model.control.state

import core.services.logger.Level
import core.services.logger.Logger

object DisconnectState : State() {
    private var prevState: app.api.dto.State? = null

    override suspend fun connect(context: AgentModelControlContext, ip: String, port: Int) {
        try {
            prevState = null
            context.modelApi.connect(ip, port)
            scheduleUpdateTask(context)
        } catch (e: Exception) {
            context.setState(DisconnectState)
            Logger.log(e.message.orEmpty(), Level.ERROR)
        }
    }

    private fun scheduleUpdateTask(context: AgentModelControlContext) {
        context.periodTaskExecutor.scheduleTask {
            updateAgentModel(context)
        }
    }

    private suspend fun updateAgentModel(context: AgentModelControlContext) {
        try {
            val inputData = context.getInputData()
            val snapshot = context.modelApi.getSnapshot(inputData)
            setState(snapshot.state, context)
            context.onUpdate(snapshot)
        } catch (e: Exception) {
            context.disconnect()
            Logger.log(e.message.orEmpty(), Level.ERROR)
        }
    }

    private fun setState(state: app.api.dto.State, context: AgentModelControlContext) {
        if (prevState == state) return
        when(state) {
            app.api.dto.State.RUN -> setRunState(context)
            app.api.dto.State.PAUSE -> setPauseState(context)
            app.api.dto.State.STOP -> setStopState(context)
        }
        if (prevState == null) {
            context.onConnect(state)
        }
        prevState = state
    }

    private fun setStopState(context: AgentModelControlContext) {
        context.periodTaskExecutor.stop()
        context.setState(StopState)
    }

    private fun setRunState(context: AgentModelControlContext) {
        context.setState(RunState)
    }

    private fun setPauseState(context: AgentModelControlContext) {
        context.periodTaskExecutor.pause()
        context.setState(PauseState)
    }
}