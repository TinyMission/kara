package kara.app

import java.net.URLClassLoader
import java.io.*
import kara.server.FileWatchListener
import java.util.ArrayList
import kara.server.FileWatcher
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
class AppLoader(val appConfig : AppConfig) : FileWatchListener {

    val logger = Logger.getLogger(this.javaClass)!!

    var classLoader : URLClassLoader? = null

    /** This lock is held while loading and retreiving the application to avoid someone retrieving an invalid application. **/
    val appLock : jet.Any = Object()

    var application : Application? = null
//        set(value) = synchronized(appLock) {$application = value}
//        get() = synchronized(appLock) {$application}

    val listeners : MutableList<AppLoadListener> = ArrayList<AppLoadListener>()

    val watcher = FileWatcher(File(appConfig.appRoot, "tmp").toString(), "restart.txt")
    val watchExecutor = Executors.newFixedThreadPool(1)

    public fun addListener(listener : AppLoadListener) {
        listeners.add(listener)
    }

    override fun onFileWatch(dir: String, fileName : String) {
        this@AppLoader.loadApp()
    }

    public fun init() {
        watcher.addListener(this)
        watchExecutor.execute(watcher)
    }


    /** Loads the application object from the filesystem.
     */
    public fun loadApp() {
        if (classLoader != null) {
            classLoader?.close()
            classLoader = null
        }

        synchronized(appLock) {
            // load the application class
            val cp = buildClasspath()
            logger.debug("Application classpath: " + cp)
            classLoader = URLClassLoader(cp, javaClass.getClassLoader())
            val appClassObject = classLoader?.loadClass("${appConfig.appPackage}.Application")
            if (appClassObject == null)
                throw RuntimeException("Expected class ${appConfig.appPackage}.Application to be defined")
            val appClass = appClassObject as Class<Application>
            val cons = appClass.getConstructor(javaClass<AppConfig>())
            application = cons.newInstance(appConfig)
            logger.debug("Application class: ${application.javaClass.toString()}")

            for (listener in listeners) {
                listener.onAppLoaded(application as Application)
            }
        }
    }

    private fun buildClasspath() : Array<URL> {
        val answer = ArrayList<URL>()
        answer.add(File(appConfig.appRoot, "bin").toURI().toURL())
        appendJars(File(appConfig.appRoot, "lib"), answer)
        return Array<URL>(answer.size) {answer.get(it)}
    }

    private fun appendJars(dir: File, answer : MutableList<URL>) {
        dir.listFiles()?.forEach { file ->
            val name = file.getName()
            when {
                name == "src" || name == "sources" -> {}
                name.endsWith("-src.jar") || name.endsWith("-sources.jar") -> {}
                file.isDirectory() -> appendJars(file, answer)
                name.endsWith(".jar") -> {
                    answer.add(file.toURI().toURL())
                }
                else -> {}
            }
        }
    }
}
