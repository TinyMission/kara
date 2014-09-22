package kara

import java.net.URLClassLoader
import java.io.*
import java.util.ArrayList
import java.util.concurrent.Executors
import java.util.concurrent.Executor
import org.apache.log4j.Logger
import java.net.URL
import kara.*
import kara.internal.*

/** Controls the loading of a Kara app from a directory.
 */
class ApplicationLoader(val applicationConfig: ApplicationConfig)  {
    val loadedHandlers: MutableList<(Application) -> Unit> = ArrayList<(Application) -> Unit>()

    public fun loaded(handler: (Application) -> Unit) {
        loadedHandlers.add(handler)
    }

    /** Loads the application object from the filesystem.
     */
    public fun load(): Application {
        val defaultClassLoader = javaClass.getClassLoader()!!
        val classLoader = URLClassLoader(applicationConfig.classPath, defaultClassLoader)
        val appClassObject = classLoader.loadClass(applicationConfig.applicationClassName)
        if (appClassObject == null)
            throw RuntimeException("Expected class ${applicationConfig.applicationClassName} to be defined")
        val applicationClass = appClassObject as Class<Application>
        val cons = applicationClass.getConstructor(javaClass<ApplicationConfig>())
        val application = cons.newInstance(applicationConfig) as Application
        Application.logger.debug("Application class: ${application.javaClass.toString()}")
        application.start()
        for (loadedHandler in loadedHandlers) {
            loadedHandler(application)
        }
        return application
    }
}
