package kara

import org.reflections.Reflections
import java.util.regex.Pattern
import java.util.ArrayList
import org.reflections.util.ClasspathHelper
import java.net.URLClassLoader
import org.reflections.util.ConfigurationBuilder
import org.reflections.scanners.SubTypesScanner
import kara.internal.*

/** The base Kara application class.
 */
abstract class Application(protected val config: AppConfig, private vararg val routes : Any) {
    private var _dispatcher : Dispatcher? = null
    public val dispatcher : Dispatcher
        get() {
            var d = _dispatcher
            if (d == null) {
                d = buildDispatcher()
                _dispatcher = d
            }
            return d!!
        }

    private fun buildDispatcher() : Dispatcher {
        val routeClasses = if (routes.size == 0) {
            scanPackage("${config.appPackage}.routes", this.javaClass.getClassLoader()!!)
        }
        else {
            scanObjects(*routes)
        }

        return Dispatcher(routeClasses)
    }

    fun resetDispatcher() {
        _dispatcher = null
    }
}
