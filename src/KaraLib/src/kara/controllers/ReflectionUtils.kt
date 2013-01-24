package kara.controllers

import java.lang.reflect.Modifier

fun Class<*>.objectInstance() : Any? {
    try {
        val field = getDeclaredField("\$instance")
        if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
            return field.get(null)!!
        }
        return null
    }
    catch (e : NoSuchFieldException) {
        return null
    }
}

fun Class<*>.routePrefix() : String {
    val owner = getEnclosingClass()
    val defaultPart = if (owner == null) "" else getSimpleName().toLowerCase()
    val part = getAnnotation(javaClass<Path>())?.path ?: defaultPart

    val base = if (owner == null) "" else owner.routePrefix()
    return base.appendPathElement(part)
}

fun Class<out Request>.route() : Pair<String, HttpMethod> {
    fun p(part : String) = (getEnclosingClass()?.routePrefix()?:"").appendPathElement(part.replace("#", getSimpleName().toLowerCase()))
    for (ann in getAnnotations()) {
        when (ann) {
            is Get -> return Pair(p(ann.route), HttpMethod.GET)
            is Post -> return Pair(p(ann.route), HttpMethod.POST)
            is Put -> return Pair(p(ann.route), HttpMethod.PUT)
            is Delete -> return Pair(p(ann.route), HttpMethod.DELETE)
            else -> Unit.VALUE
        }
    }

    throw RuntimeException("No HTTP method annotation found in ${getName()}")
}
