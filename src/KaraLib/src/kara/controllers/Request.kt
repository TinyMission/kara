package kara.controllers

import kara.controllers.Get
import kara.controllers.Post
import java.net.URL
import kara.controllers.ActionResult
import kara.controllers.BaseController
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest
import java.lang.reflect.Modifier
import kara.views.ActionContext
import kara.util.*
import java.util.HashSet
import java.util.LinkedHashSet

public open class Request(private val handler: ActionContext.() -> ActionResult) {
    fun handle(context : ActionContext) : ActionResult {
        return context.handler()
    }

    public fun toExternalForm() : String {
        val (route, method) = javaClass.route()
        if (method != HttpMethod.GET) throw RuntimeException("External form only supported for GET requests")

        val answer = StringBuilder()

        val properties = LinkedHashSet(properties())

        answer.append(route.routeComps().map({
            when (it) {
                is StringRouteComp -> (it as RouteComp).compString
                is ParamRouteComp -> {
                    properties.remove(it.name)

                    // TODO: introduce serializers similar to deserializers in appconfig
                    "${propertyValue(it.name)}"
                }
                is WildcardRouteComp -> throw RuntimeException("Routes with wildcards aren't supported")
                else -> throw RuntimeException("Unknown route component $it of class ${it.javaClass.getName()}")
            }
        }).join("/"))

        if (properties.size > 0) {
            answer.append("?")
            answer.append(properties map { "$it=${propertyValue(it)}" } join("&"))
        }

        return answer.toString()
    }
}
