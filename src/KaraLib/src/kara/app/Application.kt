package kara

import java.util.regex.Pattern
import java.util.ArrayList
import java.net.URLClassLoader
import kara.internal.*
import java.net.URL
import java.io.File
import org.apache.log4j.Logger

/** The base Kara application class.
 */
abstract class Application(val config: AppConfig, private vararg val routes : Any) {
    private var _dispatcher : ActionDispatcher? = null
    private var lastRequestServedAt: Long = 0
    public val dispatcher : ActionDispatcher
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

    private fun buildDispatcher() : ActionDispatcher {
        val newClassloader = config.requestClassloader(javaClass.getClassLoader()!!)
        if (routes.size != 0) {
            return ActionDispatcher(this, scanObjects(routes, newClassloader))
        }

        val resourceFinder = {
            (url: String) -> when {
                url.startsWith("/resources") -> {
                    try {
                        val classname = url.substring(0, url.lastIndexOf('.')).trimLeading("/resources/")
                        val res = Class.forName(classname, true, newClassloader).objectInstance()
                        if (res is Resource) res as Resource else null
                    } catch(e: ClassNotFoundException) {
                        null
                    }

                }
                else -> null
            }
        }


        // Discover routes via reflections
        val routePackages = config.routePackages ?: listOf("${config.appPackage}.routes", "${config.appPackage}.styles");
        return ActionDispatcher(this,
                routePackages.flatMap { scanPackageForRequests(it, newClassloader) },
                resourceFinder)
    }

    open fun shutDown() {

    }
}
