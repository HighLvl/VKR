package app.services.logger

data class Log(val text: String, val level: Level) {
    enum class Level {
        ERROR, INFO, DEBUG
    }
}