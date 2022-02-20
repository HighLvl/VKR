package app.services.model.control.state

import app.services.model.control.state.AgentModelControlContext

sealed class State {
    open fun run(context: AgentModelControlContext, periodSec: Float) {}
    open fun pause(context: AgentModelControlContext) {}
    open fun resume(context: AgentModelControlContext) {}
    open fun stop(context: AgentModelControlContext) {}
    open fun onThisStateChanged(context: AgentModelControlContext) {}
}