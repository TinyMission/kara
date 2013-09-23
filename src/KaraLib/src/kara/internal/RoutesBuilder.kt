package kara.internal

import org.reflections.Reflections
import java.util.ArrayList
import kara.*
import org.reflections.util.ClasspathHelper

fun scanPackageForResources(prefix : String, classloader : ClassLoader) : List<Class<out Resource>> {
    try {
        val reflections = Reflections(prefix, classloader)
        return listOf(javaClass<Put>(), javaClass<Get>(), javaClass<Post>(), javaClass<Delete>(), javaClass<Route>(), javaClass<Location>()).flatMap {
            reflections.getTypesAnnotatedWith(it)!!.toList().filter { javaClass<Resource>().isAssignableFrom(it) }.map { it as Class<Resource> }
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
        val newClass = classloader?.loadClass(routesObject.javaClass.getName()) ?: routesObject.javaClass
        for (innerClass in newClass.getDeclaredClasses()) {
            val objectInstance = innerClass.objectInstance()
            if (objectInstance != null) {
                scan(objectInstance)
            }
            else if (javaClass<Request>().isAssignableFrom(innerClass)) {
                answer.add(innerClass as Class<Resource>)
            }
        }
    }

    for (o in objects) scan(o)

    return answer
}
