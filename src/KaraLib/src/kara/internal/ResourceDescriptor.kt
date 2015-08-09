package kara.internal

import java.lang.reflect.Method
import javax.servlet.http.*
import java.util.ArrayList
import java.lang.reflect.Type
import java.lang.reflect.Constructor
import java.net.URLDecoder
import kara.*
import kotlinx.reflection.*
import org.apache.log4j.Logger
import java.io.IOException
import kotlin.properties.*

val logger = Logger.getLogger(javaClass<ResourceDescriptor>())!!

/** Contains all the information necessary to match a route and execute an action.
 */
class ResourceDescriptor(val httpMethod: HttpMethod, val route: String, val resourceClass: Class<out Resource>, val allowCrossOrigin: String?) {

    private val routeComponents = route.toRouteComponents()

    // TODO: verify optional components are all last
    private val optionalComponents by lazy { routeComponents.filter { it is OptionalParamRouteComponent }.toList() }

    public fun matches(url: String): Boolean {
        val path = url.substringBefore("?")
        val components = path.toPathComponents()
        if (components.size() > routeComponents.size() || components.size() < routeComponents.size() - optionalComponents.size())
            return false

        for (i in components.indices) {
            val component = components[i]
            val routeComponent = routeComponents[i]
            if (!routeComponent.matches(component))
                return false
        }
        return true
    }

    public fun buildParams(request: HttpServletRequest): RouteParameters {
        val url = request.requestURI?.removePrefix(request.contextPath.orEmpty())!!
        val query = request.queryString
        val params = RouteParameters()

        // parse the route parameters
        val pathComponents = url.substringBefore('?').toPathComponents().map { urlDecode(it) }
        if (pathComponents.size() < routeComponents.size() - optionalComponents.size())
            throw InvalidRouteException("URL has less components than mandatory parameters of the route")
        for (i in pathComponents.indices) {
            val component = pathComponents[i]
            val routeComponent = routeComponents[i]
            routeComponent.setParameter(params, component)
        }

        // parse query parameters
        query?.split('&')?.map {urlDecode(it)}?.forEach {
            val (name, value) = it.partition('=')
            params[name] = value.orEmpty()
        }

        // parse the form parameters
        for (formParameterName in request.parameterNames) {
            val value = request.getParameter(formParameterName)!!
            params[formParameterName] = value
        }

        if (request.contentType?.startsWith("multipart/form-data")?:false) {
            for (part in request.parts!!) {
                if (part.size < 4192) {
                    val name = part.name!!
                    params[name] = part.inputStream?.buffered()?.reader()?.readText()?:""
                }
            }
        }

        return params
    }

    /** Execute the action based on the given request and populate the response. */
    public fun exec(context: ApplicationContext, request: HttpServletRequest, response: HttpServletResponse) {
        val params = buildParams(request)
        val routeInstance = try {
            resourceClass.buildBeanInstance(params._map)
        }
        catch(e: RuntimeException) {
            throw e
        }
        catch (e: Exception) {
            throw RuntimeException("Error processing ${request.method} ${request.requestURI}, parameters={${params.toString()}}, User agent: ${request.getHeader("User-Agent")}", e)
        }

        val actionContext = ActionContext(context, request, response, params)

        actionContext.withContext {
            val actionResult = when {
                allowCrossOrigin == "" && params[ActionContext.SESSION_TOKEN_PARAMETER] != actionContext.sessionToken() ->
                    ErrorResult(403, "This request is only valid within same origin")
                else -> {
                    if(!allowCrossOrigin.isNullOrEmpty()) {
                        response.addHeader("Access-Control-Allow-Origin", allowCrossOrigin)
                    }
                    routeInstance.handle(actionContext)
                }
            }

            actionResult.writeResponse(actionContext)
        }
    }

    public override fun toString(): String {
        return "Resource<${resourceClass}> at $route"
    }

}

