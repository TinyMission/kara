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
abstract class Application(protected val config: AppConfig, private vararg val routes : Any) {
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
            return ActionDispatcher(config, scanObjects(routes, newClassloader))
        }

        // Discover routes via reflections
        val routePackages = config.routePackages;
        if (routePackages == null) {
            // routePackages are not specified in appConfig, use default app.routes package
            val defaultRoutes = "${config.appPackage}.routes"
            return ActionDispatcher(config, scanPackage(defaultRoutes, newClassloader))
        }

        return ActionDispatcher(config, routePackages.flatMap { scanPackage(it, newClassloader) })
    }
}
