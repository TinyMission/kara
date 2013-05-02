package kara.internal

import java.lang.reflect.Method
import javax.servlet.http.*
import java.util.ArrayList
import java.lang.reflect.Type
import jet.runtime.typeinfo.JetValueParameter
import java.lang.reflect.Constructor
import java.net.URLDecoder
import kara.*


/** Contains all the information necessary to match a route and execute an action.
*/
class ActionDescriptor(val route : String, val requestClass: Class<out Request>) {
    class object {
        /** Deserializes parameters into objects. */
        public val paramDeserializer: ParamDeserializer = ParamDeserializer()
    }

    private val routeComps = route.toRouteComponents()

    public fun matches(url : String) : Boolean {
        val path = url.split("\\?")[0]
        val comps = path.split("/")
        if (comps.size != routeComps.size())
            return false
        for (i in comps.indices) {
            val comp = comps[i]
            val routeComp = routeComps[i]
            if (!routeComp.matches(comp))
                return false
        }
        return true
    }

    public fun getParams(request : HttpServletRequest) : RouteParameters {
        val url = request.getRequestURI()!!
        val query = request.getQueryString()
        val params = RouteParameters()

        // parse the query string
        if (query != null) {
            val queryComps = query.split("\\&") map { URLDecoder.decode(it, "UTF-8")}
            for (qc in queryComps) {
                val nvp = qc.split("=")
                if (nvp.size > 1)
                    params[nvp[0]] = nvp[1]
                else
                    params[nvp[0]] = ""
            }
        }

        // parse the route parameters
        val comps = url.split("/") map { URLDecoder.decode(it, "UTF-8")}
        if (comps.size != routeComps.size())
            throw RuntimeException("URL has different number of components than route")
        for (i in comps.indices) {
            val comp = comps[i]
            val routeComp = routeComps[i]
            routeComp.getParam(params, comp)
        }

        // parse the form parameters
        for (name in request.getParameterNames()) {
            val value = request.getParameter(name)!!
            params[name] = value
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

    /** Execute the action based on the given request and populate the response. */
    public fun exec(appConfig: AppConfig, request: HttpServletRequest, response : HttpServletResponse) {
        val params = getParams(request)
        val routeConstructor = requestClass.getConstructors()[0] as Constructor<Request>

        val paramTypes = routeConstructor.getParameterTypes()!!
        val annotations = routeConstructor.getParameterAnnotations()

        fun find(list : Array<Annotation>) : JetValueParameter {
            for (a in list) {
                if (a is JetValueParameter) return a
            }
            throw RuntimeException("Missing Kotlin runtime annotations!");
        }

        val paramValues : Array<Any?> = Array(paramTypes.size) { i ->
            val annotation = find(annotations[i]!!)
            val paramName = annotation.name()!!
            val optional = annotation.`type`()?.startsWith("?") ?: false
            val paramString = params[paramName]
            if (paramString == null) {
                if (optional) {
                    null
                }
                else {
                    throw RuntimeException("Required argument $paramName is missing")
                }
            }
            else {
                paramDeserializer.deserialize(paramString, paramTypes[i] as Class<Any>)
            }
        }

        val routeInstance = routeConstructor.newInstance(*paramValues)!!

        val context = ActionContext(appConfig, request, response, params)

        try {
            // run middleware with beforeRequest
            for (ref in appConfig.middleware.all) {
                if (ref.matches(request.getRequestURI()!!)) {
                    val keepGoing = ref.middleware.beforeRequest(context)
                    if (!keepGoing)
                        return
                }
            }

            val result = routeInstance.handle(context)

            // run middleware with afterRequest
            for (ref in appConfig.middleware.all) {
                if (ref.matches(request.getRequestURI()!!)) {
                    val keepGoing = ref.middleware.afterRequest(context)
                    if (!keepGoing)
                        return
                }
            }

            // write the result to the response
            result.writeResponse(context)
        }
        catch (ex : Exception) {
            // write the standard error page
            var error : Throwable = ex
            if (ex.getCause() != null)
                error = ex.getCause()!!
            ErrorView(error).writeResponse(context)
        }
    }


    public fun toString() : String {
        return "Action{route=$route, handler=${requestClass}}"
    }

}

