package app.components

import core.components.base.Component
import core.components.base.ComponentConverter
import core.components.base.ComponentSnapshot
import core.components.changePropertyValue

fun Component.getSnapshot(): ComponentSnapshot {
    return ComponentConverter.convertToComponentSnapshot(this)
}

fun Component.loadSnapshot(snapshot: ComponentSnapshot) {
    snapshot.mutableProps.forEach {
        changePropertyValue(it.name, it.value)
    }
}

