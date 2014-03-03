package kotlinx.reflection

import java.util.*
import java.lang.reflect.*
import java.beans.Introspector
import jet.runtime.typeinfo.JetValueParameter
import kotlin.Array

fun Class<*>.objectInstance(): Any? {
    try {
        val field = getDeclaredField("instance\$")
        if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
            return field.get(null)!!
        }
        return null
    }
    catch (e: NoSuchFieldException) {
        return null
    }
}

fun Class<*>.classObjectInstance(): Any? {
    try {
        val field = getDeclaredField("object\$")
        if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
            return field.get(null)!!
        }
        return null
    }
    catch (e: NoSuchFieldException) {
        return null
    }
}

fun Class<out Any>.propertyGetter(property: String): Method? {
    try {
        return getMethod("get${when {
            property.length == 1 && property[0].isLowerCase() -> property.capitalize()
            property.length > 2 && property[1].isLowerCase() -> property.capitalize()
            else -> property
        }}")
    }
    catch (e: Exception) {
        return null
    }
}

fun Any.propertyValue(property: String): Any? {
    val getter = javaClass.propertyGetter(property) ?: error("Invalid property ${property} on type ${javaClass.getName()}")
    return getter.invoke(this)
}

fun Any.properties(): List<String> {
    val answer = ArrayList<String>()

    for (method in javaClass.getDeclaredMethods()) {
        val name = method.getName()!!
        if (name.startsWith("get") && method.getParameterTypes()?.size == 0) {
            answer.add(Introspector.decapitalize(name.substring(3))!!)
        }
    }

    return answer.sort()
}

private fun find(list: Array<Annotation>): JetValueParameter {
    for (a in list) {
        if (a is JetValueParameter) return a
    }
    throw RuntimeException("Missing Kotlin runtime annotations!");
}


[suppress("UNCHECKED_CAST")]
fun <T> Class<out T>.buildBeanInstance(params: (String) -> String?): T {
    val objectInstance = this.objectInstance()
    if (objectInstance != null)
        return objectInstance as T

    val ktor = primaryConstructor()

    val paramTypes = ktor.getParameterTypes()!!
    val annotations = ktor.getParameterAnnotations()

    val arguments: Array<Any?> = Array(paramTypes.size) { i ->
        val annotation = find(annotations[i]!!)
        val paramName = annotation.name()
        val optional = annotation.`type`().startsWith("?")

        params(paramName)?.let {
            Serialization.deserialize(it, paramTypes[i] as Class<Any>)
        } ?: if (optional) {
            null
        }
        else {
            error("Required argument $paramName is missing")
        }
    }

    return ktor.newInstance(*arguments)
}

fun Any.primaryProperties() : List<String> {
    val ktor = javaClass.primaryConstructor()
    val annotations = ktor.getParameterAnnotations()

    return annotations map {
        val name = find(it!!).name()

        if (javaClass.propertyGetter(name) == null) {
            error("'$name' is missing val in ${javaClass.getName()}'s primary constructor")
        }

        name
    }
}

[suppress("UNCHECKED_CAST")]
fun <T> Class<out T>.primaryConstructor() : Constructor<T> {
    val constructors = this.getConstructors()
    return constructors[0] as Constructor<T>
}
