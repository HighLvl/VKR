package app.components

import core.components.Component
import core.components.ComponentConverter
import core.components.ComponentSnapshot
import core.components.changePropertyValue
import core.services.logger.Level
import core.services.logger.Logger

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

