package kara.internal

import java.util.ArrayList
import jet.MutableList
import kara.*
import javax.servlet.http.*
import java.lang.reflect.Method
import org.apache.log4j.Logger
import java.lang.reflect.Modifier

/** Used by the server to dispatch requests to their appropriate actions.
 */
class ActionDispatcher(val appConfig: AppConfig, routeTypes: List<Class<out Request>>) {
    private val logger = Logger.getLogger(this.javaClass)!!

    private val httpMethods = Array(HttpMethod.values().size) {
        ArrayList<ActionDescriptor>()
    };

    {
        for (routeType in routeTypes) {
            val (route, httpMethod) = routeType.route()
            httpMethods[httpMethod.ordinal()].add(ActionDescriptor(route, routeType))
        }
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

    fun dispatch(request: HttpServletRequest, response : HttpServletResponse) {
        try {
            val url = request.getRequestURI() as String
            val actionDescriptor = findDescriptor(request.getMethod()!!, url)
            if (actionDescriptor == null)
                throw NotFoundException("Could not match any routes to ${url}")
            else
                actionDescriptor.exec(appConfig, request, response)
        }
        catch (ex404 : NotFoundException) {
            val out = response.getWriter()
            out?.println("Requested resource ${request.getRequestURI()} not found.")
            out?.println(ex404.getMessage())
            out?.flush()
        }
        catch (ex : Exception) {
            val out = response.getWriter()
            println("dispatch error: ${ex.getMessage()}")
            ex.printStackTrace()
            out?.print(ex.getMessage())
            out?.flush()
        }
    }
}

