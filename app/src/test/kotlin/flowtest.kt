import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


fun main() {
    val context = Dispatchers.IO + Job()
    val viewModelScope = CoroutineScope(context)
    val controlScope = CoroutineScope(context)

    runBlocking(Dispatchers.IO) {
        viewModelScope.launch {
            delay(10000)
        }
        controlScope.launch {
            delay(10000)
        }
        delay(100)
        controlScope.coroutineContext[Job]?.children?.forEach { println(it) }
        viewModelScope.coroutineContext.cancelChildren()
        controlScope.coroutineContext[Job]?.children?.forEach { println(it) }
        delay(100)


    }
}
