package kotlinx.reflection

import java.util.*
import java.lang.reflect.*
import java.beans.Introspector
import jet.runtime.typeinfo.JetValueParameter
import kotlin.Array
import java.util.concurrent.ConcurrentHashMap
import kotlinx.reflection

object ReflectionCache {
    val objects = ConcurrentHashMap<Class<*>, Any>()
    val classObjects = ConcurrentHashMap<Class<*>, Any>()
    val consMetadata = ConcurrentHashMap<Class<*>, Triple<Constructor<*>?, Array<Class<*>>, Array<Array<Annotation>?>>>()
    val primaryProperites = ConcurrentHashMap<Class<*>, List<String>>()
    val properites = ConcurrentHashMap<Class<*>, List<String>>()
    val propertyGetters = ConcurrentHashMap<Pair<Class<*>, String>, Any>()
}

private object NullMask
private fun Any.unmask():Any? = if (this == NullMask) null else this

fun Class<*>.objectInstance(): Any? {
    return ReflectionCache.objects.getOrPut(this) {
        try {
            val field = getDeclaredField("INSTANCE\$")
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
                field[null]!!
            }
            else NullMask
        }
        catch (e: NoSuchFieldException) {
            NullMask
        }
    }.unmask()
}

fun Class<*>.classObjectInstance(): Any? {
    return ReflectionCache.classObjects.getOrPut(this) {
        try {
            val field = getDeclaredField("OBJECT\$")
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
                field[null]!!
            }
            else NullMask
        }
        catch (e: NoSuchFieldException) {
            NullMask
        }
    }.unmask()
}

fun Class<out Any>.propertyGetter(property: String): Method? {
    return ReflectionCache.propertyGetters.getOrPut(Pair(this, property)) {
        try {
            getMethod("get${when {
                property.length == 1 && property[0].isLowerCase() -> property.capitalize()
                property.length > 2 && property[1].isLowerCase() -> property.capitalize()
                else -> property
            }}")
        }
        catch (e: Exception) {
            NullMask
        }
    }.unmask() as Method?
}

fun Any.propertyValue(property: String): Any? {
    val getter = javaClass.propertyGetter(property) ?: error("Invalid property ${property} on type ${javaClass.getName()}")
    return getter.invoke(this)
}

fun Any.properties(): List<String> {
    return ReflectionCache.properites.getOrPut(javaClass) {
        val answer = ArrayList<String>()

        for (method in javaClass.getDeclaredMethods()) {
            val name = method.getName()!!
            if (name.startsWith("get") && method.getParameterTypes()?.size == 0) {
                answer.add(Introspector.decapitalize(name.substring(3))!!)
            }
        }

        answer.sort()
    }
}

private fun find(list: Array<Annotation>): JetValueParameter {
    for (a in list) {
        if (a is JetValueParameter) return a
    }
    throw RuntimeException("Missing Kotlin runtime annotations!");
}

private fun Class<*>.consMetaData(): Triple<Constructor<*>?, Array<Class<*>>, Array<Array<Annotation>?>> {
    return ReflectionCache.consMetadata.getOrPut(this) {
        val ktor = primaryConstructor()

        val paramTypes = ktor?.getParameterTypes() ?: array()
        val annotations = ktor?.getParameterAnnotations() ?: array()

        Triple(ktor, paramTypes, annotations)
    }
}

public class MissingArgumentException(val name: String) : RuntimeException("Required argument $name is missing")

[suppress("UNCHECKED_CAST")]
fun <T> Class<out T>.buildBeanInstance(params: (String) -> String?): T {
    val objectInstance = this.objectInstance()
    if (objectInstance != null)
        return objectInstance as T

    val (ktor, paramTypes, annotations) = consMetaData()
    if (ktor == null) return objectInstance() as T

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
            throw MissingArgumentException(paramName)
        }
    }

    return ktor.newInstance(*arguments) as T
}

fun Any.primaryProperties() : List<String> {
    return ReflectionCache.primaryProperites.getOrPut(javaClass) {
        val (ktor, paramTypes, annotations) = javaClass.consMetaData()

        annotations map {
            val name = find(it!!).name()

            if (javaClass.propertyGetter(name) == null) {
                error("'$name' is missing val in ${javaClass.getName()}'s primary constructor")
            }

            name
        }
    }
}

[suppress("UNCHECKED_CAST")]
fun <T> Class<out T>.primaryConstructor() : Constructor<T>? {
    return getConstructors().firstOrNull() as? Constructor<T>
}
