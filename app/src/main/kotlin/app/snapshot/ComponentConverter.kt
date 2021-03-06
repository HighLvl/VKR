package app.snapshot

import core.components.base.AddToSnapshot
import core.components.base.Component
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

object ComponentConverter {
    fun convertToComponentSnapshot(component: Component): ComponentSnapshot {
        val properties = component::class.memberProperties.filter { it.hasAnnotation<AddToSnapshot>() }
                .sortedBy {
                    it.findAnnotation<AddToSnapshot>()!!.priority
                }
        val mutableProps = mutableListOf<Property>()
        val immutableProps = mutableListOf<Property>()
        properties.forEach {
            when (it) {
                is KMutableProperty<*> -> handleKMutableProperty(it, component, mutableProps, immutableProps)
                else -> handleKProperty(it, component, mutableProps, immutableProps)
            }
        }
        return ComponentSnapshot(component::class.qualifiedName.toString(), immutableProps, mutableProps)
    }

    private fun handleKProperty(
        property: KProperty<*>,
        component: Component,
        mutableProps: MutableList<Property>,
        immutableProps: MutableList<Property>
    ) {
        val value = property.call(component)
        val type = value?.let { it::class } ?: property.returnClass
        immutableProps.add(Property(property.name, type.java, value!!))
    }

    private fun handleKMutableProperty(
        property: KMutableProperty<*>,
        component: Component,
        mutableProps: MutableList<Property>,
        immutableProps: MutableList<Property>
    ) {
        val value = property.call(component)
        val type = value?.let { it::class } ?: property.returnClass
        val v = Property(property.name, type.java, value!!)

        when (property.setter.visibility) {
            KVisibility.PRIVATE, KVisibility.INTERNAL -> immutableProps.add(v)
            KVisibility.PUBLIC -> mutableProps.add(v)
            else -> {}
        }
    }

    private val KProperty<*>.returnClass get() = returnType.classifier as KClass<*>
}