package app.services

import app.api.ApiImpl
import app.services.model.control.AgentModelControlService
import app.services.scene.SceneService
import app.services.user.Service

object ServiceManager {
    val sceneService = SceneService()
    val modelControlService = AgentModelControlService(ApiImpl(), sceneService)

    init {
        Service.agentModelControl = modelControlService
        Service.scene = sceneService.scene
    }

    fun start() {
        sceneService.start()
        modelControlService.start()
    }

    fun stop() {
        sceneService.stop()
        modelControlService.stop()
    }
}