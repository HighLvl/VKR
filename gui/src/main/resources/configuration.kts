import app.services.model.configuration.modelConfiguration

modelConfiguration {
    agentInterface("SimpleAgent") {
        setter<Int>("x")
        setter<String>("text")
        setter<Main.A>("n")

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