package model.modelcontrol.service


import io.reactivex.rxjava3.core.Observable
import core.api.AgentModelApiClient
import core.services.Service
import model.modelcontrol.service.state.AgentModelControlContext


class AgentModelControlService(apiClient: AgentModelApiClient) : Service() {
    private val context = AgentModelControlContext(apiClient)
    val availableControlActions: Observable<List<ControlAction>> = context.availableControlActions

    override fun start() = context.start()

    fun run(periodSec: Float) = context.run(periodSec)

    fun pause() = context.pause()

    fun resume() = context.resume()

    fun stop() = context.stop()

}




