package kara.internal

import java.util.ArrayList
import jet.MutableList
import kara.*
import javax.servlet.http.*
import java.lang.reflect.Method
import org.apache.log4j.Logger
import java.lang.reflect.Modifier
import java.util.HashMap

/** Used by the server to dispatch requests to their appropriate actions.
 */
class ActionDispatcher(val app: Application, routeTypes: List<Class<out Request>>, val resourceFinder: (String)->Resource? = {null}) {
    private val logger = Logger.getLogger(this.javaClass)!!

    private val httpMethods = Array(HttpMethod.values().size) {
        ArrayList<ActionDescriptor>()
    };
    private val routes = HashMap<Class<out Request>, String>()
    private val resources = HashMap<String, Resource>();

    {
        for (routeType in routeTypes) {
            val (route, httpMethod) = routeType.route()
            routes[routeType] = route
            httpMethods[httpMethod.ordinal()].add(ActionDescriptor(route, routeType))
        }
    }

    fun route(requestType: Class<out Request>): String {
        return routes[requestType] ?: requestType.route().first
    }

    /** Matches an http method and url to an ActionInfo object.
        Returns null if no match is found.
    */
    fun findDescriptor(httpMethod: String, url: String): ActionDescriptor? {
        val httpMethodIndex = httpMethod.asHttpMethod().ordinal()
        val matches = ArrayList<ActionDescriptor>(httpMethods[httpMethodIndex].filter { it.matches(url) })

        return when (matches.size()) {
            1 -> matches[0]
            0 -> null
            else -> throw InvalidRouteException("URL '${url}' matches more than single route: ${matches.map { it.route }.join(", ")}")
        }
    }

    fun findResource(url: String): Resource? {
        return resources[url] ?: resourceFinder(url)?.let {resources[url] = it; it}
    }

    fun dispatch(request: HttpServletRequest, response : HttpServletResponse): Boolean {
        val url = request.getRequestURI() as String
        val query = request.getQueryString()
        val method = request.getMethod()
        logger.info("$method -- ${request.getRequestURL()}${if (query != null) "?" + query else ""}")
        val actionDescriptor = findDescriptor(method!!, url)
        if (actionDescriptor != null) {
            actionDescriptor.exec(app, request, response)
            return true
        }
        else {
            if (method.asHttpMethod() == HttpMethod.GET) {
                val resource = findResource(url)
                if (resource != null) {
                    val content = resource.content()
                    val resp = BinaryResponse(resource.mime, content.length, content.lastModified, content.data)
                    resp.tryWriteResponse(ActionContext(app, request, response, RouteParameters()))
                    return true
                }
            }
        }
        return false
    }
}
