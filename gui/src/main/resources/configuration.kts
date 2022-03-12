import app.components.configuration.modelConfiguration

modelConfiguration {
    agentInterface("Board") {
        properties("a")
        setter<Int>("a")
        setter<String>("text")
        request<Unit>("nextStep") {
            param<Float>("dt")
        }
    }
    globalArgs(
        "doodlebugs" to 100,
        "ants" to 50
//        "observable" to listOf("x", "y", "z"),
//        "mutable" to listOf("q", "w", "r")
    )
}