package app.services

import app.api.ApiImpl
import app.services.model.control.AgentModelControlService
import app.services.scene.SceneService
import core.services.Services
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

object ServiceManager {
    val sceneService = SceneService()
    val modelControlService = AgentModelControlService(ApiImpl(), sceneService)

    init {
        initServices()
    }

    private fun initServices() {
        Services::class.memberProperties
            .first { it.name == Services::agentModelControl.name }
            .apply {
                this as KMutableProperty<*>
                isAccessible = true
                setter.call(modelControlService)
            }
        Services::class.memberProperties
            .first { it.name == Services::scene.name }
            .apply {
                this as KMutableProperty<*>
                isAccessible = true
                setter.call(sceneService.scene)
            }
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

