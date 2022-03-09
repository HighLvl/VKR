package core.entities

import core.components.Component

inline fun <reified C : Component> ComponentHolder.setComponent() = setComponent(C::class)
inline fun <reified C : Component> ComponentHolder.getComponent(): C? = getComponent(C::class)
inline fun <reified C : Component> ComponentHolder.removeComponent(): C? = removeComponent(C::class)