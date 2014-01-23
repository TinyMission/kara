package kara

import java.net.URL
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest
import java.lang.reflect.Modifier
import kara.internal.*
import kotlinx.reflection.*
import java.util.HashSet
import java.util.LinkedHashSet
import kotlin.html.*
import java.util.LinkedHashMap

public abstract class Resource() : Link {
    abstract  fun handle(context: ActionContext): ActionResult

    override fun href() = toExternalForm()

    fun requestParts(): Pair<String, Map<String, Any>> {
        val route = javaClass.fastRoute()

        val path = StringBuilder()

        val properties = LinkedHashSet(properties())
        val components = route.toRouteComponents().map({
            when (it) {
                is StringRouteComponent -> it.componentText
                is OptionalParamRouteComponent -> {
                    properties.remove(it.name)
                    Serialization.serialize(propertyValue(it.name))
                }
                is ParamRouteComponent -> {
                    properties.remove(it.name)
                    Serialization.serialize(propertyValue(it.name))
                }
                is WildcardRouteComponent -> throw RuntimeException("Routes with wildcards aren't supported")
                else -> throw RuntimeException("Unknown route component $it of class ${it.javaClass.getName()}")
            }
        })

        path.append(components.filterNotNull().join("/"))
        if (path.length() == 0) path.append("/")

        val queryArgs = LinkedHashMap<String, Any>()
        for (prop in properties filter { propertyValue(it) != null }) {
            queryArgs[prop] = propertyValue(prop)!!
        }

        return Pair(path.toString(), queryArgs)
    }

    private fun toExternalForm(): String {
        val url = requestParts()
        if (url.second.size() == 0) return url.first

        val answer = StringBuilder()

        answer.append(url.first)
        answer.append("?")
        answer.append(url.second map { "${it.key}=${Serialization.serialize(it.value)}" } join("&"))

        return answer.toString()
    }
}

private fun Class<out Resource>.fastRoute(): String {
    return ActionContext.tryGet()?.application?.dispatcher?.route(this) ?: route().first
}

public fun Class<out Resource>.baseLink(): Link {
    val route = fastRoute()
    if (route.contains(":")) {
        throw RuntimeException("You can't have base link for the route with URL parameters")
    }

    return route.link()
}

