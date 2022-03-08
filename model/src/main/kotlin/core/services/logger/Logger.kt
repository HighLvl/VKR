package core.services.logger

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

object Logger {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val _logs = MutableSharedFlow<Log>()
    val logs: Flow<Log> = _logs

    fun log(text: String, level: Level) {
        coroutineScope.launch {
            _logs.emit(Log(text, level))
        }
    }
}