package kara.internal

import kara.*
import kotlinx.reflection.filterIsAssignable
import kotlinx.reflection.findClasses
import kotlinx.reflection.kotlinCached
import java.util.*
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.full.declaredMemberFunctions

val karaAnnotations = listOf(Get::class.java, Post::class.java, Put::class.java, Delete::class.java, Route::class.java, Location::class.java)

private fun KAnnotatedElement.ok() = annotations.any { a -> karaAnnotations.any { it.isAssignableFrom(a.javaClass) } }

@Suppress("UNCHECKED_CAST")
fun scanPackageForResources(prefix: String, classloader: ClassLoader, cache: MutableMap<Pair<Int, String>, List<Class<*>>>) : List<KAnnotatedElement> {
    try {
        val classes = classloader.findClasses(prefix, cache)
        return classes.filterIsAssignable<Resource>().map { it.kotlinCached }.filter { it.ok() } +
            classes.filter{ it.isAnnotationPresent(Controller::class.java) && it.kotlinCached.objectInstance != null }.
                    flatMap { it.kotlinCached.declaredMemberFunctions.filter { it.ok() } }
    } catch(e: Throwable) {
        e.printStackTrace()
        throw RuntimeException("I'm totally failed to start up. See log :(")
    }
}

/** Test only **/
fun scanObjects(objects : Array<Any>, classloader: ClassLoader? = null) : List<KAnnotatedElement> {
    val answer = ArrayList<KAnnotatedElement>()

    @Suppress("UNCHECKED_CAST")
    fun scan(routesObject : Any) {
        val newClass = classloader?.loadClass(routesObject.javaClass.name) ?: routesObject.javaClass
        for (innerClass in newClass.declaredClasses) {
            (innerClass as Class<Any>).kotlinCached.objectInstance?.let {
                scan(it)
            } ?: run {
                if (Resource::class.java.isAssignableFrom(innerClass)) {
                    answer.add(innerClass.kotlinCached)
                }
            }
        }

        answer.addAll(newClass.kotlinCached.declaredMemberFunctions.filter { it.ok() })

    }

    for (o in objects) scan(o)

    return answer
}
