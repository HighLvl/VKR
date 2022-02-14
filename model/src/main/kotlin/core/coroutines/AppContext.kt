package core.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

object AppContext {
    lateinit var context: CoroutineContext
}

private val coroutineScope = CoroutineScope(AppContext.context)

fun launchWithAppContext(block: suspend CoroutineScope.() -> Unit): Job = coroutineScope.launch(block = block)
fun <T> Flow<T>.collectWithAppContext(collector: FlowCollector<T>) {
    launchWithAppContext {
        collect(collector)
    }
}

fun <T>FlowCollector<T>.emitWithAppContext(value: T) {
    launchWithAppContext {
        emit(value)
        println("Published $value")
    }
}