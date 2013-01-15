package kara.controllers

import java.util.ArrayList
import jet.MutableList
import org.reflections.Reflections
import kara.controllers.*
import javax.servlet.http.*
import java.lang.reflect.Method
import kotlin.nullable.makeString
import kara.exceptions.NotFoundException
import org.apache.log4j.Logger
import kara.config.AppConfig
import java.lang.reflect.Modifier

/** Used by the server to dispatch requests to their appropriate actions.
 */
class Dispatcher(val routes : Any) {
    private val logger = Logger.getLogger(this.javaClass)!!

    private val actions = Array(HttpMethod.values().size) {
        ArrayList<ActionInfo>()
    };

    {
        reset()
    }

    /** Initializes the dispatcher by reflecting through the route objects passed.
     */
    public fun reset() {
        for (actionList in actions) {
            actionList.clear()
        }

        initWithReflection(routes)
    }

    private fun initWithReflection(routesObject : Any) {
        for (innerClass in routesObject.javaClass.getDeclaredClasses()) {
            val objectInstance = innerClass.objectInstance()
            if (objectInstance != null) {
                initWithReflection(objectInstance)
            }
            else if (javaClass<Request>().isAssignableFrom(innerClass)) {
                for (ann in innerClass.getAnnotations()) {
                    val requestClass = innerClass as Class<Request>
                    val (route, method) = requestClass.route()
                    actions[method.ordinal()].add(ActionInfo(route, requestClass))
                }
            }
        }
    }

    /** Matches an http method and url to an ActionInfo object.
        Returns null if no match is found.
    */
    fun match(httpMethod : String, url : String) : ActionInfo? {
        val method = httpMethod.asHttpMethod()

        for (actionInfo in actions[method.ordinal()]) {
            if (actionInfo.matches(url)) {
                return actionInfo
            }
        }
        return null
    }

    fun dispatch(appConfig: AppConfig, request: HttpServletRequest, response : HttpServletResponse) {
        try {
            val url = request.getRequestURI() as String
            val actionInfo = match(request.getMethod() as String, url)
            if (actionInfo == null)
                throw NotFoundException("Could not match any routes to ${url}")
            else
                actionInfo.exec(appConfig, request, response)
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
