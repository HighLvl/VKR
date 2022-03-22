package app.requests

import kotlin.reflect.KClass

object ResponseTypeReference {
    fun getTypeFor(ack: Int): KClass<*> = RequestMediator.getTypeFor(ack)
}