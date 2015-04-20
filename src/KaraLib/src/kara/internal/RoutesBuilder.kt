package kara.internal

import kotlinx.reflection.*
import java.util.ArrayList
import kara.*
import org.jetbrains.kotlin.load.java.reflect.tryLoadClass
import java.lang.instrument.Instrumentation
import kotlin.reflect.jvm.kotlin

val karaAnnotations = listOf(javaClass<Put>(), javaClass<Get>(), javaClass<Post>(), javaClass<Delete>(), javaClass<Route>(), javaClass<Location>())

fun scanPackageForResources(prefix : String, classloader : ClassLoader) : List<Class<out Resource>> {
    try {
        return classloader.loadedClasses(prefix).filter {
            it.getDeclaredAnnotations().any { it in karaAnnotations }
        }.map { it as? Class<Resource> }.filterNotNull()

    }
    catch(e: Throwable) {
        e.printStackTrace()
        throw RuntimeException("I'm totally failed to start up. See log :(")
    }
}



fun scanObjects(objects : Array<Any>, classloader: ClassLoader? = null) : List<Class<out Resource>> {
    val answer = ArrayList<Class<out Resource>>()

    fun scan(routesObject : Any) {
        val newClass = classloader?.loadClass(routesObject.javaClass.getName()) ?: routesObject.javaClass
        for (innerClass in newClass.getDeclaredClasses()) {
            val objectInstance = innerClass.objectInstance()
            if (objectInstance != null) {
                scan(objectInstance)
            }
            else if (javaClass<Resource>().isAssignableFrom(innerClass)) {
                answer.add(innerClass as Class<Resource>)
            }
        }
    }

    for (o in objects) scan(o)

    return answer
}
