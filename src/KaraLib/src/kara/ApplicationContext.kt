package kara

import kara.internal.*
import kotlinx.reflection.*
import kotlin.properties.Delegates
import javax.servlet.http.*
import java.util.*
import org.apache.log4j.Logger
import org.reflections.Reflections
import java.io.IOException

/** Current application execution context
 */
class ApplicationContext(packages: List<String>, val classLoader: ClassLoader, val resourceTypes: List<Class<out Resource>>) {
    val logger = Logger.getLogger(this.javaClass)!!
    private val interceptors = ArrayList<(HttpServletRequest, HttpServletResponse, (HttpServletRequest, HttpServletResponse) -> Boolean) -> Boolean>()
    private val monitorInstances = ArrayList<ApplicationContextMonitor>();

    {
        packages.flatMap { scanPackageForMonitors(it) }.forEach {
            val objectInstance = it.objectInstance()
            if (objectInstance != null) {
                logger.info("Executing startup sequence on $objectInstance")
                val monitor = objectInstance as ApplicationContextMonitor
                monitor.created(this);
                monitorInstances.add(monitor)
            } else {
                val instance = it.newInstance()
                logger.info("Executing startup sequence on new instance of $it")
                instance.created(this)
                monitorInstances.add(instance)
            }
        }
    }

    public fun intercept(interceptor: (request: HttpServletRequest, response: HttpServletResponse, proceed: (HttpServletRequest, HttpServletResponse) -> Boolean) -> Boolean) {
        interceptors.add(interceptor)
    }

    public fun dispatch(request: HttpServletRequest, response: HttpServletResponse): Boolean {
        fun dispatch(index: Int, request: HttpServletRequest, response: HttpServletResponse): Boolean {
            return if (index in interceptors.indices) {
                interceptors[index](request, response) { req, resp -> dispatch(index + 1, req, resp) }
            }
            else {
                dispatcher.dispatch(request, response)
            }
        }

        try {
            return dispatch(0, request, response)
        }
        catch(ex: IOException) {
            // All kinds of EOFs and Broken Pipes can be safely ignored
        }
        catch(ex: Throwable) {
            Application.logger.error("Error processing request", ex)
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage())
        }

        return true
    }

    fun dispose() = monitorInstances.forEach { it.destroyed(this) }

    private var _dispatcher: ResourceDispatcher? = null

    public val dispatcher: ResourceDispatcher
        get() {
            var d = _dispatcher
            if (d == null) {
                d = ResourceDispatcher(this, resourceTypes)
                _dispatcher = d
            }
            return d!!
        }


    fun scanPackageForMonitors(prefix: String): List<Class<out ApplicationContextMonitor>> {
        try {
            return Reflections(prefix, classLoader).getSubTypesOf(javaClass<ApplicationContextMonitor>())!!.toList()
        }
        catch(e: Throwable) {
            e.printStackTrace()
            return listOf<Class<ApplicationContextMonitor>>()
        }
    }
}
