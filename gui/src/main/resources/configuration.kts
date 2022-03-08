import app.services.model.configuration.modelConfiguration

modelConfiguration {
    agentInterface("Board") {
        properties()
        setter<Int>("a")
        setter<String>("text")
    }
    globalArgs(
        "doodlebugs" to 100,
        "ants" to 50
//        "observable" to listOf("x", "y", "z"),
//        "mutable" to listOf("q", "w", "r")
    )
}