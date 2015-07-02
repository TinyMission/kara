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
import java.net.URLEncoder
import kotlin.reflect.KClass
import kotlin.reflect.jvm.java

public abstract class Resource() : Link {
    abstract  fun handle(context: ActionContext): ActionResult

    override fun href(): String = href(contextPath())

    fun href(context: String): String {
        val url = requestParts(context)
        if (url.second.size() == 0) return url.first

        val answer = StringBuilder()

        answer.append(url.first)
        answer.append("?")
        answer.append(url.second map { "${it.key}=${Serialization.serialize(it.value)?.let{urlEncode(it)}}" } join("&"))

        return answer.toString()
    }

    fun requestParts(context: String = contextPath()): Pair<String, Map<String, Any>> {
        val descriptor = javaClass.fastRoute()
        val route = descriptor.route

        val path = StringBuilder(context)

        val properties = LinkedHashSet(primaryProperties())
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

        if (descriptor.allowCrossOrigin == "") {
            queryArgs[ActionContext.SESSION_TOKEN_PARAMETER] = ActionContext.current().sessionToken()
        }

        return Pair(path.toString(), queryArgs)
    }
}

private fun Class<out Resource>.fastRoute(): ResourceDescriptor {
    return ActionContext.tryGet()?.appContext?.dispatcher?.route(this) ?: route()
}

public fun KClass<out Resource>.baseLink(): Link = java.baseLink()

public fun Class<out Resource>.baseLink(): Link {
    val descriptor = fastRoute()
    val route = descriptor.route
    if (route.contains(":")) {
        throw RuntimeException("You can't have base link for the route with URL parameters")
    }

    return (if (descriptor.allowCrossOrigin == "") {
        "$route?_st=${ActionContext.current().sessionToken()}"
    }
    else route).link()

}

public fun String.link(): Link {
    return DirectLink(appendContext())
}

public fun contextPath(): String {
    val request = ActionContext.tryGet()?.request
    if (request == null) return ""
    return request.getAttribute("CONTEXT_PATH") as? String ?: request.getContextPath() ?: ""
}

public fun HttpServletRequest.setContextPath(path: String) {
    setAttribute("CONTEXT_PATH", path)
}

public fun String.appendContext(): String {
    if (startsWith("/") && !startsWith("//")) {
        return contextPath() + this
    }

    return this
}
