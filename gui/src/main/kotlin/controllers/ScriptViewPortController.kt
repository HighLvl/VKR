package controllers

import core.services.EventBus
import core.services.Update
import views.ScriptViewPort

class ScriptViewPortController(private val scriptViewPort: ScriptViewPort): Controller() {
    override fun start() {
        super.start()
        scriptViewPort.onDrawListener = {
            EventBus.publish(Update)
        }
    }

    override fun stop() {
        super.stop()
        scriptViewPort.onDrawListener = { }
    }
}