package core.components.base

import core.entities.Entity
import kotlin.reflect.KClass

annotation class AddInSnapshot(val priority: Int = 0)

/** Определяет тип сущности (entityClass, components), к которой может быть добавлен компонент.
 * Если компонент отсоединятется от сущности, то все зависимые от него компоненты каскадно отсоединяются.
 * @param entityClass класс сущности, к которой может быть добавлен компонент
 * @param components список компонентов, от которых зависит компонент
 **/
annotation class TargetEntity(val entityClass: KClass<out Entity>, val components: Array<KClass<out Component>> = [])

interface Component

