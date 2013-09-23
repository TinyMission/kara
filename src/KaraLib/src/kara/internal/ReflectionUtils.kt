package kara.internal

import java.util.*
import java.lang.reflect.*
import java.beans.Introspector
import kara.*

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

fun String?.asNotEmpty(): String? = if (this == null) null else if (!isEmpty()) this else null

fun String.appendPathElement(part : String) : String {
    val b = StringBuilder()
    b.append(this)
    if (!this.endsWith("/")) {
        b.append("/")
    }

    if (part.startsWith('/')) {
        b.append(part.substring(1))
    }
    else {
        b.append(part)
    }

    return b.toString()
}

fun Class<*>.routePrefix(): String {
    val owner = getEnclosingClass()
    val defaultPart = if (owner == null) "" else getSimpleName().toLowerCase()
    val part = getAnnotation(javaClass<Path>())?.path.asNotEmpty() ?: defaultPart

    val base = if (owner == null) "" else owner.routePrefix()
    return base.appendPathElement(part)
}

fun Class<out Resource>.route(): Pair<String, HttpMethod> {
    fun p(part: String) = (getEnclosingClass()?.routePrefix()?:"").appendPathElement(part.replace("#", getSimpleName().toLowerCase()))
    for (ann in getAnnotations()) {
        when (ann) {
            is Get -> return Pair(p(ann.route), HttpMethod.GET)
            is Post -> return Pair(p(ann.route), HttpMethod.POST)
            is Put -> return Pair(p(ann.route), HttpMethod.PUT)
            is Delete -> return Pair(p(ann.route), HttpMethod.DELETE)
            is Route -> return Pair(p(ann.route), ann.method)
            else -> Unit.VALUE
        }
    }

    throw RuntimeException("No HTTP method annotation found in ${getName()}")
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
        throw InvalidPropertyException(this, property)
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
