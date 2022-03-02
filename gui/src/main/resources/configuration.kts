import app.services.model.configuration.modelConfiguration

modelConfiguration {
    agentInterface("Type1") {
        setter<Int>("x")
        setter<String>("text")

        request<Unit>("putToMap") {
            param<Int>("x")
            param<Float>("y")
        }
    }
    globalArgs(
        "observable" to listOf("x", "y", "z"),
        "mutable" to listOf("q", "w", "r")
    )
}