package kara

import org.reflections.Reflections

abstract class StartupProcess {
    abstract fun init();
}

fun scanPackageForStartup(prefix: String, classloader: ClassLoader): List<Class<out StartupProcess>> {
    try {
        return Reflections(prefix, classloader).getSubTypesOf(javaClass<StartupProcess>())!!.toList()
    }
    catch(e: Throwable) {
        e.printStackTrace()
        return listOf<Class<StartupProcess>>()
    }
}
