import core.components.configuration.modelConfiguration

modelConfiguration {
    inputArgs {
        arg("doodlebugs", 100)
        arg("ants", 50)
    }

    agentInterface("Array") {
        prop("array")
    }
    agentInterface("Board") {
        prop("size")
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
}