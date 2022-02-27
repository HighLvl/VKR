package core.services

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import core.api.dto.GlobalArgs
import core.api.dto.Snapshot
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
object EventBus {
    private val publisher = PublishSubject.create<Event>()

    fun publish(event: Event) {
        publisher.onNext(event)
    }

    fun <T : Event> listen(eventType: KClass<T>): Observable<T> = publisher.ofType(eventType.java)


}

inline fun <reified T : Event> EventBus.listen() = listen(T::class)

sealed class Event

sealed class AgentModelLifecycleEvent : Event() {
    object Start : AgentModelLifecycleEvent()
    object Pause : AgentModelLifecycleEvent()
    object Resume : AgentModelLifecycleEvent()
    object Stop : AgentModelLifecycleEvent()
    class Update(val modelTime: Float) : AgentModelLifecycleEvent()
    object AfterUpdate : AgentModelLifecycleEvent()
    class GlobalArgsSet(val args: GlobalArgs) : Event()
}

class SnapshotReceive(val snapshot: Snapshot) : Event()
