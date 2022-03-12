package core.entities

import core.components.base.Component
import kotlin.reflect.KClass

interface ComponentHolder {
    fun <C: Component> setComponent(type: KClass<out C>): C
    fun <C : Component> getComponent(type: KClass<C>): C?
    fun <C : Component> removeComponent(type: KClass<C>): C?
    fun getComponents(): List<Component>
}