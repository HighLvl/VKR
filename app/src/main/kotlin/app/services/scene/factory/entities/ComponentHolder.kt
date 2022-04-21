package app.services.scene.factory.entities

import core.components.base.Component
import kotlin.reflect.KClass

interface ComponentHolder {
    fun <C: Component> setComponent(type: KClass<out C>): C
    fun <C : Any> getComponent(type: KClass<C>): C?
    fun <C : Component> removeComponent(type: KClass<out C>): C?
    fun getComponents(): List<Component>
}