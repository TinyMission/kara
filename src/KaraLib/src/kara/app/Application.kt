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

            var d = _dispatcher
            if (d == null) {
                d = buildDispatcher()
                _dispatcher = d
            }
            lastRequestServedAt = System.currentTimeMillis()
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

    public fun dispatch(request: HttpServletRequest, response: HttpServletResponse): Boolean {
        fun dispatch(index: Int, request: HttpServletRequest, response: HttpServletResponse): Boolean {
            return if (index in interceptors.indices) {
                interceptors[index](request, response) { req, resp ->
                    dispatch(index + 1, req, resp)
                }
            }
            else {
                dispatcher.dispatch(request, response)
            }
        }

        return dispatch(0, request, response)
    }

    private val interceptors = ArrayList<(HttpServletRequest, HttpServletResponse, (HttpServletRequest, HttpServletResponse)->Boolean)->Boolean>()

    public fun intercept(interceptor: (request: HttpServletRequest, response: HttpServletResponse, proceed: (HttpServletRequest, HttpServletResponse)->Boolean)->Boolean) {
        interceptors.add(interceptor)
    }
}
