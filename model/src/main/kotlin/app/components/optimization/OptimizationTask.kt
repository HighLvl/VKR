package app.components.optimization

import app.logger.Log
import app.logger.Logger
import app.utils.KtsScriptEngine
import core.components.base.IgnoreInSnapshot
import core.components.base.Script

class OptimizationTask : Script() {
    var task: String = ""
        set(value) {
            field = tryLoadOptimizationTaskModel(value)
        }

    private fun tryLoadOptimizationTaskModel(path: String): String {
        if (path.isEmpty()) return ""
        return try {
            taskModel = KtsScriptEngine.eval(path)
            path
        } catch (e: Exception) {
            Logger.log("Bad optimization task file", Log.Level.ERROR)
            ""
        }
    }

    @IgnoreInSnapshot
    var taskModel: OptimizationTaskModel = OptimizationTaskModel()
        private set
}
