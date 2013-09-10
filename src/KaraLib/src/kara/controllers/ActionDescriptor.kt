package kara.internal

import java.lang.reflect.Method
import javax.servlet.http.*
import java.util.ArrayList
import java.lang.reflect.Type
import jet.runtime.typeinfo.JetValueParameter
import java.lang.reflect.Constructor
import java.net.URLDecoder
import kara.*
import org.apache.log4j.Logger
import java.io.IOException
import kotlin.properties.delegation.lazy.LazyVal


/** Contains all the information necessary to match a route and execute an action.
*/
class ActionDescriptor(val route : String, val requestClass: Class<out Request>) {
    class object {
        val logger = Logger.getLogger(this.javaClass)!!

        /** Deserializes parameters into objects. */
        public val paramDeserializer: ParamDeserializer = ParamDeserializer()
    }

    private val routeComponents = route.toRouteComponents()

    // TODO: verify optional components are all last
    private val optionalComponents by LazyVal { routeComponents.filter { it is OptionalParamRouteComponent }.toList() }


    public fun matches(url : String) : Boolean {
        val path = url.split("\\?")[0]
        val components = path.split("/")
        if (components.size > routeComponents.size() || components.size < routeComponents.size() - optionalComponents.size())
            return false

        for (i in components.indices) {
            val component = components[i]
            val routeComponent = routeComponents[i]
            if (!routeComponent.matches(component))
                return false
        }
        return true
    }

    public fun buildParams(request : HttpServletRequest) : RouteParameters {
        val url = request.getRequestURI()!!
        val query = request.getQueryString()
        val params = RouteParameters()

        // parse the query string
        if (query != null) {
            val queryComponents = query.split("\\&") map { URLDecoder.decode(it, "UTF-8")}
            for (component in queryComponents) {
                val nvp = component.split("=")
                if (nvp.size > 1)
                    params[nvp[0]] = nvp[1]
                else
                    params[nvp[0]] = ""
            }
        }

        // parse the route parameters
        val pathComponents = url.split("/") map { URLDecoder.decode(it, "UTF-8")}
        if (pathComponents.size < routeComponents.size() - optionalComponents.size())
            throw InvalidRouteException("URL has less components than mandatory parameters of the route")
        for (i in pathComponents.indices) {
            val component = pathComponents[i]
            val routeComponent = routeComponents[i]
            routeComponent.setParameter(params, component)
        }

        // parse the form parameters
        for (formParameterName in request.getParameterNames()) {
            val value = request.getParameter(formParameterName)!!
            params[formParameterName] = value
        }

        if (request.getContentType()?.startsWith("multipart/form-data")?:false) {
            for (part in request.getParts()!!) {
                if (part.getSize() < 4192) {
                    val name = part.getName()!!
                    params[name] = part.getInputStream()?.buffered()?.reader()?.readText()?:""
                }
            }
        }

        return params
    }

    fun buildRouteInstance(params : RouteParameters) : Request{
        fun find(list : Array<Annotation>) : JetValueParameter {
            for (a in list) {
                if (a is JetValueParameter) return a
            }
            throw RuntimeException("Missing Kotlin runtime annotations!");
        }

        val routeConstructor = requestClass.getConstructors()[0] as Constructor<Request>

        val paramTypes = routeConstructor.getParameterTypes()!!
        val annotations = routeConstructor.getParameterAnnotations()

        val arguments : Array<Any?> = Array(paramTypes.size) { i ->
               val annotation = find(annotations[i]!!)
               val paramName = annotation.name()!!
               val optional = annotation.`type`()?.startsWith("?") ?: false
               val paramString = params[paramName]
               if (paramString == null) {
                   if (optional) {
                       null
                   }
                   else {
                       throw InvalidRouteException("Required argument $paramName is missing")
                   }
               }
               else {
                   paramDeserializer.deserialize(paramString, paramTypes[i] as Class<Any>)
               }
           }

        return routeConstructor.newInstance(*arguments)!!
    }

    /** Execute the action based on the given request and populate the response. */
    public fun exec(app: Application, request: HttpServletRequest, response : HttpServletResponse) {
        val params = buildParams(request)
        val routeInstance = buildRouteInstance(params)
        val context = ActionContext(app, request, response, params)

        var result:ActionResult? = null
        try {
            // run middleware with beforeRequest
            for (ref in app.config.middleware.all) {
                if (ref.matches(request.getRequestURI()!!)) {
                    val keepGoing = ref.middleware.beforeRequest(context)
                    if (!keepGoing)
                        return
                }
            }

            result = routeInstance.handle(context)

            // run middleware with afterRequest
            for (ref in app.config.middleware.all) {
                if (ref.matches(request.getRequestURI()!!)) {
                    val keepGoing = ref.middleware.afterRequest(context, result!!)
                    if (!keepGoing)
                        return
                }
            }
        }
        catch (ex : Throwable) {
            logger.warn("exec error: ${ex.getMessage()}");
            ex.printStackTrace()
            // write the standard error page
            var error : Throwable = ex
            if (ex.getCause() != null)
                error = ex.getCause()!!
            ErrorView(error).writeResponse(context)
        }

        result?.tryWriteResponse(context)
    }


    public fun toString() : String {
        return "Action{route=$route, handler=${requestClass}}"
    }

}

