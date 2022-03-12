package viewmodel

import app.coroutines.Contexts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class ViewModel {
    protected val viewModelScope = CoroutineScope(Contexts.app)
    protected fun launchWithAppContext(block: suspend CoroutineScope.() -> Unit): Job =
        viewModelScope.launch(block = block)
}