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

fun Class<out Resource>.route(): ResourceDescriptor {
    fun p(part: String) = (getEnclosingClass()?.routePrefix()?:"").appendPathElement(part.replace("#", getSimpleName().toLowerCase()))
    for (ann in getAnnotations()) {
        when (ann) {
            is Get -> return ResourceDescriptor(HttpMethod.GET, p(ann.route), this, null)
            is Post -> return ResourceDescriptor(HttpMethod.POST, p(ann.route), this, ann.allowCrossOrigin)
            is Put -> return ResourceDescriptor(HttpMethod.PUT, p(ann.route), this, ann.allowCrossOrigin)
            is Delete -> return ResourceDescriptor(HttpMethod.DELETE, p(ann.route), this, ann.allowCrossOrigin)
            is Route -> return ResourceDescriptor(ann.method, p(ann.route), this, ann.allowCrossOrigin)
        }
    }

    throw RuntimeException("No HTTP method annotation found in ${getName()}")
}
