package core.components

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

object ComponentConverter {
    fun convertToComponentSnapshot(component: Component): ComponentSnapshot {
        val properties = component::class.memberProperties.filter { it.hasAnnotation<AddInSnapshot>() }
                .sortedBy {
                    it.findAnnotation<AddInSnapshot>()!!.priority
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

        if (property.setter.visibility == KVisibility.PRIVATE)
            immutableProps.add(v)
        else {
            mutableProps.add(v)
        }
    }

    private val KProperty<*>.returnClass get() = returnType.classifier as KClass<*>
}