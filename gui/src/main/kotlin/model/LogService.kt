package model

import core.services.logger.Level
import core.services.logger.Log
import core.services.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class LogService {
    private val logs = LinkedList<Log>()
    private var filteredLogs: Sequence<Log> = getFilteredLogs()
    private val filterByLevel = mutableSetOf(Level.DEBUG, Level.ERROR, Level.INFO)
    private var filterSubstring = ""
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _logsFlow = MutableStateFlow<List<Log>>(listOf())
    val logsFlow = _logsFlow.asStateFlow()

    init {
        coroutineScope.launch {
            Logger.logs.collect {
                collectLog(it)
                notifyLogsChanged()
            }
        }
    }

    private fun notifyLogsChanged() {
        _logsFlow.value = filteredLogs.toList()
    }

    private fun collectLog(log: Log) {
        val (text, level) = log
        val lines = text.split('\n').toMutableList()
        lines[0] = formatLog(lines[0], level)
        lines.forEach {
            if (logs.size < MAX_LOG_LIST_SIZE)
                logs.add(Log(it, level))
        }
        if (logs.size == MAX_LOG_LIST_SIZE) {
            repeat(lines.size) {
                if (logs.isNotEmpty()) logs.removeFirst()
            }
        }
    }


    private fun formatLog(text: String, level: Level): String {
        val date = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS")
        val dateText = date.format(formatter)
        return when (level) {
            Level.ERROR -> FORMATTED_ERROR_LOG.format(dateText, text)
            Level.INFO -> FORMATTED_INFO_LOG.format(dateText, text)
            Level.DEBUG -> FORMATTED_DEBUG_LOG.format(dateText, text)
        }
    }

    private fun getFilteredLogs(): Sequence<Log> {
        return logs.asSequence().filter { (text, level) ->
            level in filterByLevel && filterSubstring in text
        }
    }

    fun filter(levels: Set<Level>, substring: String) {
        filterByLevel.apply {
            clear()
            addAll(levels)
        }
        filterSubstring = substring
        notifyLogsChanged()
    }

    fun clear() {
        logs.clear()
        notifyLogsChanged()
    }

    private companion object {
        const val MAX_LOG_LIST_SIZE = 10000
        const val FORMATTED_ERROR_LOG = "<%s> [error] %s"
        const val FORMATTED_INFO_LOG = "<%s> [info] %s"
        const val FORMATTED_DEBUG_LOG = "<%s> [debug] %s"
    }
}