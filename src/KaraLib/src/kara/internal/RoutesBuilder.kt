package kara.internal

import org.reflections.Reflections
import java.util.ArrayList
import kara.*

fun scanPackage(prefix : String, classloader : ClassLoader) : List<Class<out Request>> {
    return Reflections(prefix, classloader).getSubTypesOf(javaClass<Request>())!!.toList()
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
