package core.utils

import kotlinx.coroutines.runBlocking

interface Observable<T> {
    fun observe(observer: suspend (T) -> Unit): Disposable
}

interface Disposable {
    fun dispose()
}

interface ValueObservable<T> : Observable<T> {
    val value: T
}

open class PublishSubject<T> : Observable<T> {
    private val observers = mutableListOf<suspend (T) -> Unit>()

    override fun observe(observer: suspend (T) -> Unit): Disposable {
        observers.add(observer)
        return object : Disposable {
            override fun dispose() {
                observers.remove(observer)
            }
        }
    }

    open suspend fun publish(value: T) {
        observers.forEach { it(value) }
    }
}

class EmptyPublishSubject : PublishSubject<Unit>() {
    fun observe(observer: () -> Unit) {
        observe { _ -> observer() }
    }

    suspend fun publish() {
        publish(Unit)
    }
}

class MutableValue<T>(initialValue: T) : PublishSubject<T>(), ValueObservable<T> {
    override var value: T = initialValue
        private set

    override suspend fun publish(value: T) {
        if (this.value == value) return
        this.value = value
        super.publish(value)
    }

    override fun observe(observer: suspend (T) -> Unit): Disposable {
        val disposable = super.observe(observer)
        runBlocking { observer(value) }
        return disposable
    }
}

