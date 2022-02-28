package controllers

import app.logger.Log
import app.logger.Logger
import core.components.Component
import core.components.Script
import core.scene.Scene
import views.ScriptViewPort

class ScriptViewPortController(scriptViewPort: ScriptViewPort, scene: Scene) {
    init {
        scriptViewPort.onDrawListener = {
            val components = scene.agents.flatMap { it.value.getComponents() } +
                    scene.environment.getComponents() +
                    scene.experimenter.getComponents()
            components.forEach {
                it.callUpdateFunIfScript()
            }
        }
    }

    private fun Component.callUpdateFunIfScript() {
        if (this is Script) {
            try {
                this.update()
            } catch (e: Exception) {
                Logger.log(e.toString(), Log.Level.ERROR)
                e.printStackTrace()
            }
        }
    }
}