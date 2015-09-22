package kara.internal

import kara.*
import kotlinx.reflection.filterIsAssignable
import kotlinx.reflection.findClasses
import kotlinx.reflection.objectInstance
import java.util.*

val karaAnnotations = listOf(Put::class.java, Get::class.java, Post::class.java, Delete::class.java, Route::class.java, Location::class.java)

@Suppress("UNCHECKED_CAST")
fun scanPackageForResources(prefix: String, classloader: ClassLoader, cache: MutableMap<Pair<Int, String>, List<Class<*>>>) : List<Class<out Resource>> {
    try {
        return classloader.findClasses(prefix, cache)
                .filterIsAssignable<Resource>()
                .filter {
                    clazz -> karaAnnotations.any { clazz.isAnnotationPresent(it) }
                }
    }
    catch(e: Throwable) {
        e.printStackTrace()
        throw RuntimeException("I'm totally failed to start up. See log :(")
    }
}



fun scanObjects(objects : Array<Any>, classloader: ClassLoader? = null) : List<Class<out Resource>> {
    val answer = ArrayList<Class<out Resource>>()

    fun scan(routesObject : Any) {
        val newClass = classloader?.loadClass(routesObject.javaClass.name) ?: routesObject.javaClass
        for (innerClass in newClass.declaredClasses) {
            innerClass.objectInstance()?.let {
                scan(it)
            } ?: run {
                if (Resource::class.java.isAssignableFrom(innerClass)) {
                    answer.add(innerClass as Class<Resource>)
                }
            }
        }
    }

    for (o in objects) scan(o)

    return answer
}
