package app.services.model.control.state

import app.services.model.control.state.AgentModelControlContext.ControlRequest.GET_STATE
import core.services.logger.Level
import core.services.logger.Logger

object DisconnectState : State() {

    override suspend fun connect(
        context: AgentModelControlContext,
        ip: String,
        port: Int,
        onResult: suspend (Result<Unit>) -> Unit
    ) {
        try {
            context.modelApi.connect(ip, port)
            context.sendControlRequest(GET_STATE, onResult)
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
            context.update()
            val requests = context.commitRequests()
            val responses = context.modelApi.handleRequests(requests)
            context.handleResponses(responses)
        } catch (e: Exception) {
            context.disconnect()
            Logger.log(e.message.orEmpty(), Level.ERROR)
        }
    }
}