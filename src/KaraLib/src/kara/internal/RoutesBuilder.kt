package kara.internal

import kotlinx.reflection.*
import java.util.ArrayList
import kara.*
import java.lang.instrument.Instrumentation
import kotlin.reflect.jvm.kotlin

val karaAnnotations = listOf(javaClass<Put>(), javaClass<Get>(), javaClass<Post>(), javaClass<Delete>(), javaClass<Route>(), javaClass<Location>())

@suppress("UNCHECKED_CAST")
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
                if (javaClass<Resource>().isAssignableFrom(innerClass)) {
                    answer.add(innerClass as Class<Resource>)
                }
            }
        }
    }

    for (o in objects) scan(o)

    return answer
}
