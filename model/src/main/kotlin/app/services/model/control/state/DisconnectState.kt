package app.services.model.control.state

object DisconnectState : State() {
    override suspend fun connect(context: AgentModelControlContext, ip: String, port: Int): core.api.dto.State {
        val state = context.apiClient.connect(ip, port)
        when (state) {
            core.api.dto.State.RUN -> {
                StopState.restoreRun(context)
            }
            core.api.dto.State.PAUSE -> {
                StopState.restorePause(context)
            }
            core.api.dto.State.STOP -> {
                context.setState(StopState)
            }
        }
        return state
    }
}