package core.components.base

import core.entities.Entity
import kotlin.reflect.KClass

@Target(AnnotationTarget.PROPERTY)
annotation class AddToSnapshot(val priority: Int = 0)

/** Определяет тип сущности (entityClass, components), к которой может быть добавлен компонент.
 * Если компонент отсоединяется от сущности, то все зависимые от него компоненты каскадно отсоединяются.
 * @param entityClass класс сущности, к которой может быть добавлен компонент
 * @param components список компонентов, от которых зависит компонент
 **/
@Target(AnnotationTarget.CLASS)
annotation class TargetEntity(
    val entityClass: KClass<out Entity>,
    val components: Array<KClass<out Any>> = []
)

abstract class Component {
    protected open suspend fun onModelRun() {}
    protected open suspend fun onModelUpdate() {}
    protected open suspend fun onModelAfterUpdate() {}
    protected open suspend fun onModelStop() {}
    protected open fun updateUI() {}
    protected open suspend fun onModelPause() {}
    protected open suspend fun onModelResume() {}
    protected open fun onAttach() {}
    protected open fun onDetach() {}
}

