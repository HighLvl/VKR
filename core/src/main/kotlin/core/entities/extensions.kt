package core.entities

import core.components.base.Component

inline fun <reified C : Component> Entity.setComponent() = setComponent(C::class)
inline fun <reified C : Any> Entity.getComponent(): C? = getComponent(C::class)
inline fun <reified C : Component> Entity.removeComponent(): C? = removeComponent(C::class)