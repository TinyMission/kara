package kara.controllers

import org.reflections.Reflections
import java.util.ArrayList

fun scanPackage(prefix : String, classloader : ClassLoader) : List<Class<out Request>> {
    return Reflections(prefix, classloader).getSubTypesOf(javaClass<Request>())!!.toList()
}

fun scanObjects(vararg objects : Any) : List<Class<out Request>> {
    val answer = ArrayList<Class<out Request>>()

    fun scan(routesObject : Any) {
        for (innerClass in routesObject.javaClass.getDeclaredClasses()) {
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
