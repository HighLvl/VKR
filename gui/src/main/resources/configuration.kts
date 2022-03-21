import core.components.configuration.modelConfiguration

modelConfiguration {
    agentInterface("Array") {
        prop("array")
    }
    agentInterface("Board") {
        structProp("size") {
            prop("height")
            prop("width")
        }
        prop("a")

        setter<Int>("a")
    }
    agentInterface("Ant") {
        prop("colPosition")
        prop("rowPosition")
        prop("breedThreshold")
        prop("timeSinceBreed")
    }

    agentInterface("Doodlebug") {
        prop("colPosition")
        prop("rowPosition")
        prop("breedThreshold")
        prop("timeSinceBreed")
        prop("timeSinceEat")
    }

    inputArgs(
        "doodlebugs" to 100,
        "ants" to 50
//        "observable" to listOf("x", "y", "z"),
//        "mutable" to listOf("q", "w", "r")
    )
}