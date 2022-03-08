package viewmodel

import core.services.logger.Log
import kotlinx.coroutines.flow.map
import model.LogService

enum class Level {
    ERROR, DEBUG, INFO
}

class LoggerViewModel(private val logService: LogService) {
    val logs = logService.logsFlow.map { logSequence -> logSequence.map(this::mapToViewLog).toList() }

    private fun mapToViewLog(log: Log) = log.text to log.level.mapToViewLogLevel()

    private fun core.services.logger.Level.mapToViewLogLevel() = when (this) {
        core.services.logger.Level.INFO -> Level.INFO
        core.services.logger.Level.ERROR -> Level.ERROR
        core.services.logger.Level.DEBUG -> Level.DEBUG
    }

    private fun mapToLoggerLevel(level: Level) = when (level) {
        Level.INFO -> core.services.logger.Level.INFO
        Level.DEBUG -> core.services.logger.Level.DEBUG
        Level.ERROR -> core.services.logger.Level.ERROR
    }

    fun clear() = logService.clear()
    fun filter(levels: Set<Level>, substring: String) =
        logService.filter(levels.asSequence().map(this::mapToLoggerLevel).toSet(), substring)
}