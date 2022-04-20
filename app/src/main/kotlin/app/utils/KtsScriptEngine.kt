package app.utils

import java.io.File
import javax.script.ScriptEngineManager

class KtsScriptEngine {
    private val engine = ScriptEngineManager().getEngineByExtension("kts")!!

    fun <T>eval(path: String): T {
        return engine.eval(File(path).bufferedReader()) as T
    }
}