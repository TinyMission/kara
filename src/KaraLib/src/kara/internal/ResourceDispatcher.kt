package kara.internal

import kara.*
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/** Used by the server to dispatch requests to their appropriate actions.
 */
class ResourceDispatcher(val context: ApplicationContext, resourceTypes: List<Class<out Resource>>) {
    private val httpMethods = Array(HttpMethod.values().size) {
        ArrayList<ResourceDescriptor>()
    };
    private val resources = HashMap<Class<out Resource>, ResourceDescriptor>()

    init {
        for (routeType in resourceTypes) {
            val descriptor = routeType.route()
            resources[routeType] = descriptor
            httpMethods[descriptor.httpMethod.ordinal].add(descriptor)
        }
    }

    fun route(requestType: Class<out Resource>): ResourceDescriptor {
        return resources[requestType] ?: requestType.route()
    }

    /** Matches an http method and url to an ActionInfo object. Returns null if no match is found.
     */
    fun findDescriptor(httpMethod: String, url: String): ResourceDescriptor? {
        val httpMethodIndex = httpMethod.asHttpMethod().ordinal
        val matches = ArrayList(httpMethods[httpMethodIndex].filter { it.matches(url) })

        return when (matches.size) {
            1 -> matches[0]
            0 -> null
            else -> throw InvalidRouteException("URL '$url' matches more than single route: ${matches.map { it.route }.joinToString(", ")}")
        }
    }

    fun dispatch(request: HttpServletRequest, response: HttpServletResponse, resourceDescriptor: ResourceDescriptor?): Boolean {
        if (resourceDescriptor != null) {
            resourceDescriptor.exec(context, request, response)
            return true
        }
        return false
    }
}
