package app.components.experiment

import core.components.SystemComponent
import app.logger.Log
import app.logger.Logger
import app.utils.KtsScriptEngine
import core.components.IgnoreInSnapshot
import core.components.Script

class Experiment : SystemComponent(), Script {
    var task: String = ""
        set(value) {
            field = tryLoadExperimentTaskModel(value)
        }

    private fun tryLoadExperimentTaskModel(path: String): String {
        if (path.isEmpty()) return ""
        return try {
            taskModel = KtsScriptEngine.eval(path)
            path
        } catch (e: Exception) {
            Logger.log("Bad experiment task file", Log.Level.ERROR)
            ""
        }
    }

    @IgnoreInSnapshot
    var taskModel: ExperimentTaskModel = ExperimentTaskModel()
        private set
}
