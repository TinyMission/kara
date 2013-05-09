package kara.internal

import org.reflections.Reflections
import java.util.ArrayList
import kara.*
import org.reflections.util.ClasspathHelper

fun scanPackageForRequests(prefix : String, classloader : ClassLoader) : List<Class<out Request>> {
    val reflections = Reflections(prefix, classloader)
    return listOf(javaClass<Put>(), javaClass<Get>(), javaClass<Post>(), javaClass<Delete>(), javaClass<Path>()).flatMap {
        reflections.getTypesAnnotatedWith(it)!!.toList().filter { javaClass<Request>().isAssignableFrom(it) }.map { it as Class<Request> }
    }
}

fun scanObjects(objects : Array<Any>, classloader: ClassLoader? = null) : List<Class<out Request>> {
    val answer = ArrayList<Class<out Request>>()

    fun scan(routesObject : Any) {
        val newClass = classloader?.loadClass(routesObject.javaClass.getName()) ?: routesObject.javaClass
        for (innerClass in newClass.getDeclaredClasses()) {
            val objectInstance = innerClass.objectInstance()
            if (objectInstance != null) {
                scan(objectInstance)
            }
            else if (javaClass<Request>().isAssignableFrom(innerClass)) {
                answer.add(innerClass as Class<Request>)
            }
        }
    }

    for (o in objects) scan(o)

    return answer
}
