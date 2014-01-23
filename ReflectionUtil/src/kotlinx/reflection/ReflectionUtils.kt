package kotlinx.reflection

import java.util.*
import java.lang.reflect.*
import java.beans.Introspector
import jet.runtime.typeinfo.JetValueParameter
import jet.Array

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

fun Class<Any>.propertyGetter(property: String): Method {
    try {
        return getMethod("get${when {
            property.length == 1 && property[0].isLowerCase() -> property.capitalize()
            property.length > 2 && property[1].isLowerCase() -> property.capitalize()
            else -> property
        }}")
    }
    catch (e: Exception) {
        error("Invalid property ${property} on type ${getName()}");
    }
}

fun Any.propertyValue(property: String): Any? {
    val getter = javaClass.propertyGetter(property)
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

[suppress("UNCHECKED_CAST")]
fun <T> Class<out T>.buildBeanInstance(params: (String) -> String?): T {
    fun find(list: Array<Annotation>): JetValueParameter {
        for (a in list) {
            if (a is JetValueParameter) return a
        }
        throw RuntimeException("Missing Kotlin runtime annotations!");
    }

    val objectInstance = this.objectInstance()
    if (objectInstance != null)
        return objectInstance as T

    val constructors = this.getConstructors()
    val ktor = constructors[0] as Constructor<T>

    val paramTypes = ktor.getParameterTypes()!!
    val annotations = ktor.getParameterAnnotations()

    val arguments: Array<Any?> = Array(paramTypes.size) { i ->
        val annotation = find(annotations[i])
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
