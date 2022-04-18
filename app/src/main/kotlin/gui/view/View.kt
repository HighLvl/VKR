package gui.view

import gui.utils.Contexts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

abstract class View {
    private val coroutineScope = CoroutineScope(Contexts.ui)
    protected fun launchWithUiContext(block: suspend CoroutineScope.() -> Unit): Job = coroutineScope.launch(block = block)

    protected fun <T> Flow<T>.collectWithUiContext(collector: FlowCollector<T>) {
        launchWithUiContext {
            collect(collector)
        }
    }
}