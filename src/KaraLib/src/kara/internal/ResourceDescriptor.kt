@file:Suppress("UNCHECKED_CAST")

package kara.internal

import kara.*
import kotlinx.reflection.Serialization
import kotlinx.reflection.boundReceiver
import kotlinx.reflection.buildBeanInstance
import kotlinx.reflection.urlDecode
import org.slf4j.LoggerFactory
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation

val logger = LoggerFactory.getLogger(ResourceDescriptor::class.java)!!

private infix fun <A, B, C> Pair<A, B>.and(c: C) = Triple(first, second, c)

/** Contains all the information necessary to match a route and execute an action.
 */
class ResourceDescriptor(val httpMethod: HttpMethod, val route: String,
                         val resourceClass: KClass<*>,
                         val resourceFun: (Map<String, String>) -> Resource,
                         val allowCrossOrigin: String?) {

    private val routeComponents = route.toRouteComponents()

    // TODO: verify optional components are all last
    private val optionalComponents by lazy { routeComponents.filter { it is OptionalParamRouteComponent }.toList() }

    fun matches(url: String): Boolean {
        val path = url.substringBefore("?")
        val components = path.toPathComponents()
        if (components.size > routeComponents.size || components.size < routeComponents.size - optionalComponents.size)
            return false

        for (i in components.indices) {
            val component = components[i]
            val routeComponent = routeComponents[i]
            if (!routeComponent.matches(component))
                return false
        }
        return true
    }

    fun buildParams(request: HttpServletRequest): RouteParameters {
        val url = request.requestURI?.removePrefix(request.contextPath.orEmpty())!!
        val params = RouteParameters()

        // parse the route parameters
        val pathComponents = url.substringBefore('?').toPathComponents().map { urlDecode(it) }
        if (pathComponents.size < routeComponents.size - optionalComponents.size)
            throw InvalidRouteException("URL has less components than mandatory parameters of the route")
        for (i in pathComponents.indices) {
            val component = pathComponents[i]
            val routeComponent = routeComponents[i]
            routeComponent.setParameter(params, component)
        }

        val isMultiPart = request.contentType?.startsWith("multipart/form-data") ?: false
        if (isMultiPart) {
            for (part in request.parts!!) {
                if (part.size < 4192) {
                    val name = part.name!!
                    params[name] = part.inputStream?.use { it.bufferedReader().readText() } ?: ""
                }
            }
        }

        // parse the request parameters
        val parameterNames = request.parameterNames.toList().filter {
            !isMultiPart || it !in params.parameterNames() // Skip parameters already loaded above on multi part initialization
        }

        for (formParameterName in parameterNames) {
            request.getParameterValues(formParameterName)?.forEach {
                params[formParameterName] = it
            }
        }

        return params
    }

    /** Execute the action based on the given request and populate the response. */
    fun exec(context: ApplicationContext, request: HttpServletRequest, response: HttpServletResponse) {
        val params = buildParams(request)

        val resource = resourceFun(params._map)
        val actionContext = ActionContext(context, request, response, params, resourceClass.findAnnotation<NoSession>() == null)

        actionContext.withContext {
            val actionResult = when {
                allowCrossOrigin == "" && params.optListParam(ActionContext.SESSION_TOKEN_PARAMETER)?.distinct()?.singleOrNull() != actionContext.sessionToken() ->
                    ErrorResult(403, "This request is only valid within same origin")
                else -> {
                    if(!allowCrossOrigin.isNullOrEmpty()) {
                        response.addHeader("Access-Control-Allow-Origin", allowCrossOrigin)
                    }
                    try {
                        resource.handle(actionContext)
                    } catch (e : ResultWithCodeException) {
                        TextResult(Serialization.serialize(e.result).orEmpty(), e.code)
                    }
                }
            }
            actionContext.flushSessionCache()
            actionResult.writeResponse(actionContext)
        }
    }

    companion object {
        fun fromResourceClass(clazz : KClass<out Resource>, ann : Annotation) : ResourceDescriptor {
            val (method, crossOrigin, route) = extractFromAnnotation(ann)
            val resolvedRoute = clazz.java.enclosingClass?.routePrefix().orEmpty()
                    .appendPathElement(route.replace("#", clazz.simpleName!!.toLowerCase()))
            return ResourceDescriptor(method, resolvedRoute, clazz, { clazz.buildBeanInstance(it) }, crossOrigin)
        }

        fun fromFunction(func : KFunction<Any>, ann: Annotation) : ResourceDescriptor {
            val (method, crossOrigin, route) = extractFromAnnotation(ann)
            val resolvedRoute = func.boundReceiver()?.javaClass?.routePrefix().orEmpty()
                    .appendPathElement(route.replace("#", func::class.simpleName!!.toLowerCase()))

            return ResourceDescriptor(method, resolvedRoute, func::class, { FunctionWrapperResource(func, it) }, crossOrigin)
        }

        private fun extractFromAnnotation(ann: Annotation): Triple<HttpMethod, String?, String> {
            return when (ann) {
                is Get -> HttpMethod.GET to null and ann.route
                is Post -> HttpMethod.POST to ann.allowCrossOrigin and ann.route
                is Put -> HttpMethod.PUT to ann.allowCrossOrigin and ann.route
                is Delete -> HttpMethod.DELETE to ann.allowCrossOrigin and ann.route
                is Route -> ann.method to ann.allowCrossOrigin and ann.route
                else -> error("Unsupported annotation $ann")
            }
        }
    }

    override fun toString(): String {
        return "Resource<$resourceClass> at $route"
    }

}

