package core.components

import core.components.base.ComponentSnapshot
import core.components.base.Component
import core.entities.base.ComponentHolder
import core.components.base.ComponentConverter
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

fun Component.changePropertyValue(propertyName: String, value: Any) {
    val property = this::class.memberProperties.first { propertyName == it.name } as KMutableProperty<*>
    property.setter.call(this, value)
}

fun Component.getSnapshot(): ComponentSnapshot {
    return ComponentConverter.convertToComponentSnapshot(this)
}

fun Component.loadSnapshot(snapshot: ComponentSnapshot) {
    snapshot.mutableProps.forEach {
        changePropertyValue(it.name, it.value)
    }
}

fun <C1, R> ComponentHolder.query(t1: KClass<C1>, block: (C1) -> R): R? where C1 : Component {
    val c1 = getComponent(t1) ?: return null
    return block(c1)
}

inline fun <reified C : Component> ComponentHolder.getComponent(): C? {
    return getComponent(C::class)
}