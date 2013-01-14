package kara.controllers

import java.lang.reflect.Method
import javax.servlet.http.*
import java.util.ArrayList
import kara.config.AppConfig
import kara.views.HtmlView
import java.lang.reflect.Type
import kara.views.ActionContext
import kara.views.ErrorView
import kara.views.ErrorLayout
import jet.runtime.typeinfo.JetValueParameter
import java.lang.reflect.Constructor
import kara.views.HtmlLayout
import kara.util.propertyValue


/** Contains all the information necessary to match a route and execute an action.
*/
class ActionInfo(val route : String, val requestClass: Class<Request>) {
    private val routeComps = route.routeComps()

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

    public fun getParams(request : HttpServletRequest) : RouteParams {
        val url = request.getRequestURI()!!
        val query = request.getQueryString()
        val params = RouteParams()

        // parse the query string
        if (query != null) {
            val queryComps = query.split("\\&")
            for (qc in queryComps) {
                val nvp = qc.split("=")
                if (nvp.size > 1)
                    params[nvp[0]] = nvp[1]
                else
                    params[nvp[0]] = ""
            }
        }

        // parse the route parameters
        val comps = url.split("/")
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

        return params
    }

    /** Execute the action based on the given request and populate the response. */
    public fun exec(val appConfig: AppConfig, request: HttpServletRequest, response : HttpServletResponse) {
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
                appConfig.paramDeserializer.deserialize(paramString, paramTypes[i] as Class<Any>)
            }
        }

        val routeInstance = routeConstructor.newInstance(*paramValues)!!

        val context = ActionContext(appConfig, request, response, params)

        try {
            val result = routeInstance.handle(context)

            // set the html layout, if applicable (this is a bit of a hack, but I'm not sure how to do it better)
            if (result is HtmlView) {
                (result as HtmlView).layout = layout()
            }

            // write the result to the response
            result.writeResponse(context)
        }
        catch (ex : Exception) {
            // write the standard error page
            var error : Throwable = ex
            if (ex.getCause() != null)
                error = ex.getCause()!!
            val errorView = ErrorView(error)
            errorView.layout = ErrorLayout()
            errorView.writeResponse(context)
        }
    }

    private fun layout() : HtmlLayout? {
        var cur : Class<*>? = requestClass;
        while (cur != null) {
            val l = layout(cur!!.objectInstance())
            if (l != null) return l
            cur = cur!!.getEnclosingClass()
        }
        return null
    }

    private fun layout(instance : Any?) : HtmlLayout? {
        if (instance == null) return null
        try {
            val value = instance.propertyValue("layout")
            if (value is HtmlLayout) return value;
        }
        catch (e: Exception) {
            // ignore
        }

        return null
    }

    public fun toString() : String {
        return "Action{route=$route, handler=${requestClass}}"
    }

}

