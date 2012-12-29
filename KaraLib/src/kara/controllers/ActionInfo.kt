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


/** Contains all the information necessary to match a route and execute an action.
*/
class ActionInfo(val route : String, val controller : BaseController, val method : Method) {

    val routeComps = route.split("/").map {
        it -> RouteComp.create(it)
    }

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
        controller.beforeRequest(request, response, params)
        val context = ActionContext(appConfig, request, response, params)

        // assemble the arguments
        val paramTypes = method.getParameterTypes()!!
        if (params.size() < paramTypes.size) {
            throw RuntimeException("Method ${method.getName()} has more arguments (${paramTypes.size}) than route parameters (${params.size()})")
        }
        val args = Array<Any>(paramTypes.size, {i ->
            val paramString  = params[i] as String
            appConfig.paramDeserializer.deserialize(paramString, paramTypes[i] as Class<Any>)
        })

        // execute the action
        try {
            val result = when (args.size) {
                0 -> method.invoke(controller) as ActionResult
                1 -> method.invoke(controller, args[0]) as ActionResult
                2 -> method.invoke(controller, args[0], args[1]) as ActionResult
                3 -> method.invoke(controller, args[0], args[1], args[2]) as ActionResult
                else -> throw RuntimeException("Unable to execute methods with ${args.size} arguments")
            }

            // set the html layout, if applicable (this is a bit of a hack, but I'm not sure how to do it better)
            if (result is HtmlView) {
                (result as HtmlView).layout = controller.layout
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

    public fun toString() : String {
        return "Action{route=$route, controller=${controller.javaClass}, method=${method.getName()}}"
    }

}

