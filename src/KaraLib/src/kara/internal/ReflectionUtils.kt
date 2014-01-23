package kara.internal

import kara.*

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
    val part = getAnnotation(javaClass<Location>())?.path.asNotEmpty() ?: defaultPart

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
