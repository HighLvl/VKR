package core.entities

import core.components.base.Component
import core.components.base.TargetEntity
import core.components.base.filterAvailableComponents
import core.utils.isSubclass
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

interface Entity {
    fun <C : Component> setComponent(type: KClass<out C>): C
    fun <C : Component> getComponent(type: KClass<C>): C?
    fun <C : Component> removeComponent(type: KClass<C>): C?
    fun getComponents(): List<Component>
}