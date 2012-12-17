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

/** Used by the server to dispatch requests to their appropriate actions.
 */
class Dispatcher(packageName : String) {

    val logger = Logger.getLogger(this.javaClass)!!

    val packageName : String = packageName

    val getActions : MutableList<ActionInfo> = ArrayList<ActionInfo>()
    val postActions : MutableList<ActionInfo> = ArrayList<ActionInfo>()
    val putActions : MutableList<ActionInfo> = ArrayList<ActionInfo>()
    val deleteActions : MutableList<ActionInfo> = ArrayList<ActionInfo>()

    /** Initializes the dispatcher by reflecting through the controllers in the app's package.
     */
    public fun initWithReflection() {
        logger.info("initializing dispatcher for package $packageName")
        val reflections = Reflections(packageName)
        val subTypes = reflections.getSubTypesOf(kara.controllers.BaseController().javaClass)!!
        for (subType in subTypes) {
            if (subType != null) {
                parseController(subType as Class<kara.controllers.BaseController>)
            }
        }
    }

    /** Matches an http method and url to an ActionInfo object.
        Returns null if no match is found.
    */
    fun match(httpMethod : String, url : String) : ActionInfo? {
        val actions = when (httpMethod) {
            "GET" -> getActions
            "POST" -> postActions
            "PUT" -> putActions
            "DELETE" -> deleteActions
            else -> getActions
        }
        for (actionInfo in actions) {
            if (actionInfo.matches(url)) {
                return actionInfo
            }
        }
        return null
    }

    fun dispatch(request: HttpServletRequest, response : HttpServletResponse) {
        try {
            val url = request.getRequestURI() as String
            val actionInfo = match(request.getMethod() as String, url)
            if (actionInfo == null)
                throw NotFoundException("Could not match any routes to ${url}")
            else
                actionInfo.exec(request, response)
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

    /** Parses an action from a method annotation */
    fun parseAction(annString : String, controller : BaseController, method : Method) {
        // get the route based on the annotation and the controller
        var route = annString.substring(annString.indexOf('=')+1, annString.indexOf(')'))
        if (!annString.contains("=") || route == "[default]") {
            route = method.getName()?.toLowerCase() as String
        }
        val root = controller.root
        if (!route.startsWith("/")) {
            route = root + route
        }

        val actionInfo = ActionInfo(route, controller, method)
        logger.debug("adding action: " + actionInfo.toString());
        if (annString.contains("@kara.controllers.Get")) {
            getActions.add(actionInfo)
        }
        else if (annString.contains("@kara.controllers.Post")) {
            postActions.add(actionInfo)
        }
        else if (annString.contains("@kara.controllers.Put")) {
            putActions.add(actionInfo)
        }
        else if (annString.contains("@kara.controllers.Delete")) {
            deleteActions.add(actionInfo)
        }
    }

    /** Finds all actions in the given controller class. */
    public fun parseController(controllerClass : Class<kara.controllers.BaseController>) {
        // create the controller object and set its root
        val controller = controllerClass.newInstance()
        logger.debug("Parsing controller ${controllerClass.getName()} with root ${controller.root}");

        // parse the controller methods
        val methods = controller.javaClass.getMethods()
        for (method in methods) {
            for (ann in method.getAnnotations()) {
                val annString = ann.toString()
                if (annString.contains("@kara.controllers.")) {
                    parseAction(annString, controller, method)
                }
            }
        }
    }
}
