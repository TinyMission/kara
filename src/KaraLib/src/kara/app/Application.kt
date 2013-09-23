package kara

import java.util.regex.Pattern
import java.util.ArrayList
import java.net.URLClassLoader
import kara.internal.*
import java.net.URL

import javax.servlet.*
import java.io.File
import org.apache.log4j.Logger
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.properties.*

/** The base Kara application class.
 */
abstract class Application(val config: ApplicationConfig, private vararg val routes: Any) {
    val logger = Logger.getLogger(this.javaClass)!!
    private var lastRequestServedAt: Long = 0
    private var _context: ApplicationContext? = null
    private val contextLock = Object()

    val context: ApplicationContext
        get() = synchronized(contextLock) {
            val now = System.currentTimeMillis()
            if (config.isDevelopment()) {
                if (now - lastRequestServedAt > 300.toLong()) {
                    _context?.dispose()
                    _context = null
                }
            }

            var context = _context
            if (context == null) {
                context = createContext()
                _context = context
            }
            lastRequestServedAt = System.currentTimeMillis()
            context!!
        }

    public fun requestClassloader(current: ClassLoader): ClassLoader =
            if (config.isDevelopment()) {
                URLClassLoader(config.classPath, current)
            } else
                current

    open fun createContext(): ApplicationContext {
        val classLoader = requestClassloader(javaClass.getClassLoader()!!)
        val resourceTypes = if (routes.size != 0) {
            scanObjects(routes, classLoader)
        } else {
            config.routePackages.flatMap { scanPackageForRequests(it, classLoader) }
        }

        return ApplicationContext(config.routePackages, classLoader, resourceTypes)
    }

    open fun start() {}

    open fun shutDown() {
        _context?.dispose()
    }
}
