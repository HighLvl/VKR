package app.logger

import kotlinx.coroutines.flow.MutableSharedFlow
import core.coroutines.launchWithAppContext
import kotlinx.coroutines.flow.Flow

object Logger {
    private val _logs = MutableSharedFlow<Log>()
    val logs: Flow<Log> = _logs

    fun log(text: String, level: Log.Level) {
        launchWithAppContext {
            _logs.emit(Log(text, level))
        }
    }

}