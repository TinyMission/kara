package kara

import kara.internal.ResourceDispatcher
import kotlinx.reflection.MissingArgumentException
import kotlinx.reflection.filterIsAssignable
import kotlinx.reflection.findClasses
import org.apache.log4j.Logger
import java.net.SocketException
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/** Current application execution context
 */
class ApplicationContext(public val config : ApplicationConfig,
                         packages: List<String>,
                         val classLoader: ClassLoader,
                         val reflectionCache: MutableMap<Pair<Int, String>, List<Class<*>>>,
                         val resourceTypes: List<Class<out Resource>>) {
    val logger = Logger.getLogger(this.javaClass)!!
    private val interceptors = ArrayList<(HttpServletRequest, HttpServletResponse, (HttpServletRequest, HttpServletResponse) -> Boolean) -> Boolean>()
    private val monitorInstances = ArrayList<ApplicationContextMonitor>();

    public val version: Int = ++versionCounter

    init {
        packages.flatMap { scanPackageForMonitors(it) }.map {
            it.kotlin.objectInstance as? ApplicationContextMonitor ?: it.newInstance()
        }.sortedBy { it.priority }.forEach { monitor ->
            logger.info("Executing startup sequence on ${monitor.javaClass}")
            monitor.created(this)
            monitorInstances.add(monitor)
        }
    }

    public fun intercept(interceptor: (request: HttpServletRequest, response: HttpServletResponse, proceed: (HttpServletRequest, HttpServletResponse) -> Boolean) -> Boolean) {
        interceptors.add(interceptor)
    }

    public fun dispatch(request: HttpServletRequest, response: HttpServletResponse): Boolean {

        fun formatLogErrorMsg(error: String, req: HttpServletRequest) = "$error processing ${req.method} ${req.requestURI}. User agent: ${req.getHeader("User-Agent")}, Referer: ${req.getHeader("Referer")}"

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
        catch(ex: SocketException) {
            // All kinds of EOFs and Broken Pipes can be safely ignored
        }
        catch(e400: MissingArgumentException) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e400.message)
            Application.logger.warn(formatLogErrorMsg("400", request), e400)
        } catch(e400: InvalidRequestException) {
            Application.logger.warn(formatLogErrorMsg("400", request), e400)
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e400.message)
        } catch(e404: NotFoundException) {
            Application.logger.warn(formatLogErrorMsg("404", request), e404)
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e404.message)
        }
        catch(ex: Throwable) {
            when {
                ex.javaClass.name == "org.apache.catalina.connector.ClientAbortException" -> {} // do nothing for tomcat specific exception
                else -> {
                    Application.logger.error(formatLogErrorMsg("Error", request), ex)
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.message)
                }
            }
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
            return d
        }


    fun scanPackageForMonitors(prefix: String): List<Class<ApplicationContextMonitor>> {
        try {
            return classLoader.findClasses(prefix, reflectionCache).filterIsAssignable<ApplicationContextMonitor>()
        }
        catch(e: Throwable) {
            e.printStackTrace()
            return listOf()
        }
    }

    companion object {
        var versionCounter: Int = 0
    }
}
