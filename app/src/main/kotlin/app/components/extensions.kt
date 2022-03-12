package app.components

import core.components.base.Component
import app.snapshot.ComponentConverter
import app.snapshot.ComponentSnapshot
import core.services.logger.Level
import core.services.logger.Logger
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

fun Component.getSnapshot(): ComponentSnapshot {
    return ComponentConverter.convertToComponentSnapshot(this)
}

fun Component.loadSnapshot(snapshot: ComponentSnapshot) {
    snapshot.mutableProps.forEach {
        try {
            changePropertyValue(it.name, it.value)
        } catch (e: Exception) {
            Logger.log(e.cause?.message.toString(), Level.ERROR)
        }
    }
}

private fun Component.changePropertyValue(propertyName: String, value: Any) {
    val property = this::class.memberProperties.first { propertyName == it.name } as KMutableProperty<*>
    property.setter.call(this, value)
}

