package app.components

import core.components.Component
import core.components.ComponentConverter
import core.components.ComponentSnapshot
import core.components.changePropertyValue

fun Component.getSnapshot(): ComponentSnapshot {
    return ComponentConverter.convertToComponentSnapshot(this)
}

fun Component.loadSnapshot(snapshot: ComponentSnapshot) {
    snapshot.mutableProps.forEach {
        changePropertyValue(it.name, it.value)
    }
}

