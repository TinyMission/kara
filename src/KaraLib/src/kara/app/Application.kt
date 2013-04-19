package kara

import org.reflections.Reflections
import java.util.regex.Pattern
import java.util.ArrayList
import org.reflections.util.ClasspathHelper
import java.net.URLClassLoader
import org.reflections.util.ConfigurationBuilder
import org.reflections.scanners.SubTypesScanner
import kara.internal.*
import java.net.URL
import java.io.File

/** The base Kara application class.
 */
abstract class Application(protected val config: AppConfig, private vararg val routes : Any) {
    private var _dispatcher : Dispatcher? = null
    private var lastRequestServedAt: Long = 0
    public val dispatcher : Dispatcher
        get() {
            val now = System.currentTimeMillis()

            if (config.isDevelopment()) {
                if (now - lastRequestServedAt > 300.toLong()) {
                    _dispatcher = null
                }
            }

            lastRequestServedAt = now

            var d = _dispatcher
            if (d == null) {
                d = buildDispatcher()
                _dispatcher = d
            }
            return d!!
        }

    private fun buildDispatcher() : Dispatcher {
        val newClassloader = config.requestClassloader(javaClass.getClassLoader()!!)
        val routeClasses = if (routes.size == 0) {
            scanPackage("${config.appPackage}.routes", newClassloader)
        }
        else {
            scanObjects(routes, newClassloader)
        }

        return Dispatcher(routeClasses)
    }
}
