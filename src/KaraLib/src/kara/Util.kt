package kara.util

import java.util.*
import kara.exceptions.InvalidPropertyException
import java.lang.reflect.Method
import java.beans.Introspector

/** Maps a list by applying a function to each element. */
fun <T, R> List<T>.map(transform : (T) -> R) : List<R> {
    val result = ArrayList<R>()
    for (item in this)
        result.add(transform(item))
    return result
}


/** Joins an array of strings with a separator. */
fun Array<String>.join(separator : String) : String {
    val builder = StringBuilder()
    for (i in this.indices) {
        builder.append(this[i])
        if (i < this.size-1)
            builder.append(separator)
    }
    return builder.toString()
}

/** Joins a list of strings with a separator. */
fun List<String>.join(separator : String) : String {
    val builder = StringBuilder()
    for (item in this) {
        builder.append(item)
        if (item != this.last())
            builder.append(separator)
    }
    return builder.toString()
}

fun Class<Any>.propertyGetter(property : String) : Method {
    try {
        return getMethod("get${if (property.length > 2 && property[1].isLowerCase() ) property.capitalize() else property}")
    }
    catch (e : Exception) {
        throw InvalidPropertyException(this, property)
    }
}

fun Any.propertyValue(property : String) : Any? {
    val getter = javaClass.propertyGetter(property)
    return getter.invoke(this)
}

fun Any.properties() : List<String> {
    val answer = ArrayList<String>()

    for (method in javaClass.getDeclaredMethods()) {
        val name = method.getName()!!
        if (name.startsWith("get") && method.getParameterTypes()?.size == 0) {
            answer.add(Introspector.decapitalize(name.substring(3))!!)
        }
    }

    return answer
}
