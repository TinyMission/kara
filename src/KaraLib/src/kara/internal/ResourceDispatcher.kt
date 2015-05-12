package kara.internal

import java.util.ArrayList
import kotlin.MutableList
import kara.*
import javax.servlet.http.*
import java.lang.reflect.Method
import org.apache.log4j.Logger
import java.lang.reflect.Modifier
import java.util.HashMap

/** Used by the server to dispatch requests to their appropriate actions.
 */
class ResourceDispatcher(val context: ApplicationContext, resourceTypes: List<Class<out Resource>>) {
    private val httpMethods = Array(HttpMethod.values().size()) {
        ArrayList<ResourceDescriptor>()
    };
    private val resources = HashMap<Class<out Resource>, String>()

    init {
        for (routeType in resourceTypes) {
            val (route, httpMethod) = routeType.route()
            resources[routeType] = route
            httpMethods[httpMethod.ordinal()].add(ResourceDescriptor(route, routeType))
        }
    }

    fun route(requestType: Class<out Resource>): String {
        return resources[requestType] ?: requestType.route().first
    }

    /** Matches an http method and url to an ActionInfo object. Returns null if no match is found.
     */
    fun findDescriptor(httpMethod: String, url: String): ResourceDescriptor? {
        val httpMethodIndex = httpMethod.asHttpMethod().ordinal()
        val matches = ArrayList(httpMethods[httpMethodIndex].filter { it.matches(url) })

        return when (matches.size()) {
            1 -> matches[0]
            0 -> null
            else -> throw InvalidRouteException("URL '${url}' matches more than single route: ${matches.map { it.route }.join(", ")}")
        }
    }

    fun dispatch(request: HttpServletRequest, response: HttpServletResponse): Boolean {
        val url = request.getRequestURI()
        val method = request.getMethod()
        val resourceDescriptor = findDescriptor(method!!, url.removePrefix(request.getContextPath().orEmpty()))
        if (resourceDescriptor != null) {
            resourceDescriptor.exec(context, request, response)
            return true
        }
        return false
    }
}
