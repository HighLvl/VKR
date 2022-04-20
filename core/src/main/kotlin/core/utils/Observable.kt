package core.utils

interface Observable<T> {
    fun observe(observer: (T) -> Unit): Disposable
}

interface Disposable {
    fun dispose()
}

interface ValueObservable<T>: Observable<T> {
    val value: T
}

open class PublishSubject<T>: Observable<T> {
    private val observers = mutableListOf<(T) -> Unit>()

    override fun observe(observer: (T) -> Unit): Disposable {
        observers.add(observer)
        return object : Disposable {
            override fun dispose() {
                observers.remove(observer)
            }
        }
    }

    fun publish(value: T) {
        observers.forEach { it(value) }
    }
}

class EmptyPublishSubject: PublishSubject<Unit>() {
    fun observe(observer: () -> Unit) {
        observe { it -> observer() }
    }

    fun publish() {
        publish(Unit)
    }
}

class MutableValue<T>(initialValue: T) : ValueObservable<T> {
    private val publishSubject =  PublishSubject<T>()
    override var value: T = initialValue
        set(value) {
            if (field == value) return
            field = value
            publishSubject.publish(value)
        }

    override fun observe(observer: (T) -> Unit): Disposable {
        val disposable = publishSubject.observe(observer)
        publishSubject.publish(value)
        return disposable
    }
}

