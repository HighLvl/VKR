package core.components.base

import core.entities.Entity
import core.utils.isSubclass
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

fun filterAvailableComponents(
    entity: Entity,
    components: List<KClass<out Component>>
): List<KClass<out Component>> {
    val filtered = mutableListOf<KClass<out Component>>()
    components.forEach { component ->
        val annotation = component.findAnnotation<TargetEntity>()
        filtered.addIfPossible(annotation, entity, component)
    }
    return filtered
}

private fun MutableList<KClass<out Component>>.addIfPossible(
    annotation: TargetEntity?,
    entity: Entity,
    component: KClass<out Component>
) {
    entity.getComponent(component)?.let { return }
    annotation?.let {
        if (!entity::class.isSubclass(annotation.entityClass)) return
        annotation.components.forEach {
            entity.getComponent(it) ?: return
        }
    }
    add(component)
}