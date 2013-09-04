package kara.app

import java.net.URLClassLoader
import java.io.*
import java.util.ArrayList
import java.util.concurrent.Executors
import java.util.concurrent.Executor
import org.apache.log4j.Logger
import java.net.URL
import kara.*
import kara.internal.*

/** Interface for object that want to listen for when an app is loaded.
 */
trait AppLoadListener {
    fun onAppLoaded(application : Application)
}


/** Controls the loading and reloading of a Kara app from a directory.
 */
class AppLoader(val appConfig : AppConfig)  {

    val logger = Logger.getLogger(this.javaClass)!!

    var classLoader : ClassLoader? = null

    /** This lock is held while loading and retreiving the application to avoid someone retrieving an invalid application. **/
    val appLock : jet.Any = Object()

    var application : Application? = null
    //        set(value) = synchronized(appLock) {$application = value}
    //        get() = synchronized(appLock) {$application}

    val listeners : MutableList<AppLoadListener> = ArrayList<AppLoadListener>()

    public fun addListener(listener : AppLoadListener) {
        listeners.add(listener)
    }

    /** Loads the application object from the filesystem.
     */
    public fun loadApp() {
        if (classLoader != null) {
            (classLoader as URLClassLoader).close()
            classLoader = null
        }

        synchronized(appLock) {
            if (application == null) {
                // load the application class
                classLoader = appConfig.applicationClassloader(javaClass.getClassLoader()!!)
                val appClassObject = classLoader?.loadClass(appConfig.appClass)
                if (appClassObject == null)
                    throw RuntimeException("Expected class ${appConfig.appClass} to be defined")
                val appClass = appClassObject as Class<Application>
                val cons = appClass.getConstructor(javaClass<AppConfig>())
                application = cons.newInstance(appConfig)
                logger.debug("Application class: ${application.javaClass.toString()}")
                application?.start()

                for (listener in listeners) {
                    listener.onAppLoaded(application as Application)
                }
            }
        }
    }
}
