package app.services.logger

import kotlinx.coroutines.flow.MutableSharedFlow
import core.coroutines.launchWithAppContext
import core.services.Service

class Logger : Service() {
    val logs = MutableSharedFlow<Log>()

    override fun start() {
        //listen errors
    }

    fun log(text: String, level: Log.Level) {
        launchWithAppContext {
            logs.emit(Log(text, level))
        }
    }

}