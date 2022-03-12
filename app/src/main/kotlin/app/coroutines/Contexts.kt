package app.coroutines

import kotlinx.coroutines.newSingleThreadContext
import kotlin.coroutines.CoroutineContext

object Contexts {
    val app: CoroutineContext = newSingleThreadContext("ApiClientContext")
}