package core.gui

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object EventBus {
    private val mutableEvents = MutableSharedFlow<UIEvent>()
    val events = mutableEvents.asSharedFlow()

    suspend fun publish(event: UIEvent) = mutableEvents.emit(event)
}

sealed class UIEvent {
    data class InspectAgent(val id: Int): UIEvent()
}
