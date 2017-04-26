package kara.internal

import kara.FunctionWrapperResource
import kara.Location
import kara.Resource
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

fun String.appendPathElement(part : String) = buildString {
    append(this@appendPathElement)
    if (!this.endsWith("/")) {
        append("/")
    }

    if (part.startsWith('/')) {
        append(part.substring(1))
    } else {
        append(part)
    }                                                                                                                                               
}

fun Class<*>.routePrefix(): String {
    val owner = enclosingClass
    val defaultPart = if (owner == null) "" else simpleName.toLowerCase()
    val part = getAnnotation(Location::class.java)?.path?.takeIf(String::isNotEmpty) ?: defaultPart

    val base = owner?.routePrefix().orEmpty()
    return base.appendPathElement(part)
}

@Suppress("UNCHECKED_CAST")
fun KAnnotatedElement.route(): ResourceDescriptor {
    fun annotation() = annotations.firstOrNull { a -> karaAnnotations.any { it.isAssignableFrom(a.javaClass) } } as? Annotation
                    ?: error("No HTTP method annotation found in ${javaClass.name}")

    return when {
        this is FunctionWrapperResource -> ResourceDescriptor.fromFunction(this.func, annotation())
        this is Resource -> ResourceDescriptor.fromResourceClass(this::class as KClass<out Resource>, annotation())
        this is KClass<*> && Resource::class.java.isAssignableFrom(this.java) ->
            ResourceDescriptor.fromResourceClass(this as KClass<out Resource>, annotation())
        this is KFunction<*> -> ResourceDescriptor.fromFunction(this as KFunction<Any>, annotation())
        else -> error("Unsupported type $this")
    }
}