package core.entities.base

import core.components.base.Component
import kotlin.reflect.KClass

interface ComponentHolder {
    fun setComponent(component: Component)
    fun <C : Component> getComponent(type: KClass<C>): C?
    fun <C : Component> removeComponent(type: KClass<C>): C?
    fun getComponents(): List<Component>
}