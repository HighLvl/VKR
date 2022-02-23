package app.services.model.control.state

abstract class ConnectState : State() {
    override suspend fun disconnect(context: AgentModelControlContext) {
        context.periodTaskExecutor.stop()
        context.apiClient.disconnect()
        context.setState(DisconnectState)
    }
}