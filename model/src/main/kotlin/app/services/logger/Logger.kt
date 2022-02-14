package app.services.logger

import kotlinx.coroutines.flow.MutableSharedFlow
import core.coroutines.launchWithAppContext
import core.services.Service
import kotlinx.coroutines.flow.Flow

class Logger : Service() {
    private val _logs = MutableSharedFlow<Log>()
    val logs: Flow<Log> = _logs

    override fun start() {
        //listen errors
    }

    fun log(text: String, level: Log.Level) {
        launchWithAppContext {
            _logs.emit(Log(text, level))
        }
    }

}