package kara

import kara.internal.*
import kotlinx.html.DirectLink
import kotlinx.html.Link
import kotlinx.reflection.*
import java.lang.reflect.InvocationTargetException
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

class ResultWithCodeException(val code: Int, val result: Any?) : Exception()

fun Any.resultWithStatusCode(statusCode: Int) {
    throw ResultWithCodeException(statusCode, this)
}

abstract class Resource : Link, KAnnotatedElement {

    @Throws(ResultWithCodeException::class)
    abstract fun handle(context: ActionContext): ActionResult

    override fun href(): String = href(contextPath())

    override val annotations: List<Annotation> get() = this::class.annotations

    protected open val properties : Map<String, Any?>
        get() = this::class.primaryConstructor?.parameters.orEmpty().
                associate { it.name!! to propertyValue<Resource, Any?>(it.name!!) }

    fun href(context: String) = buildString {
        val url = requestParts(context)
        if (url.second.isEmpty()) return url.first
        append(url.first)
        append("?")
        append((url.second.map { "${it.key}=${Serialization.serialize(it.value)?.let{urlEncode(it)}}" }).joinToString(("&")))
    }

    fun requestParts(context: String = contextPath()): Pair<String, Map<String, Any>> {
        val descriptor = fastRoute()
        val route = descriptor.route

        val path = StringBuilder(context)

        if (!context.endsWith('/')) {
            path.append('/')
        }

        val properties = properties.toMutableMap()
        val components = route.toRouteComponents().mapNotNull({
            when (it) {
                is StringRouteComponent -> it.componentText
                is OptionalParamRouteComponent -> {
                    Serialization.serialize(properties.remove(it.name))?.let(::urlEncode)
                }
                is ParamRouteComponent -> {
                    Serialization.serialize(properties.remove(it.name))?.let(::urlEncode)
                }
                is WildcardRouteComponent -> throw RuntimeException("Routes with wildcards aren't supported")
                else -> throw RuntimeException("Unknown route component $it of class ${it::class.simpleName}")
            }
        })

        path.append(components.joinToString("/"))

        val queryArgs = LinkedHashMap<String, Any>()
        for ((prop, value) in properties.filter { it.value != null}) {
            queryArgs[prop] = value!!
        }

        if (descriptor.allowCrossOrigin == "") {
            queryArgs[ActionContext.SESSION_TOKEN_PARAMETER] = ActionContext.current().sessionToken()
        }

        return path.toString() to queryArgs
    }
}

internal class FunctionWrapperResource(val func: KFunction<Any>, val params: Map<String, String>) : Resource() {
    private val owner: Any by lazy { func.boundReceiver()!! }

    override fun handle(context: ActionContext): ActionResult = try {
        val _contentType = owner::class.findAnnotation<Controller>()!!.contentType
        val (code, result) = try {
            HttpServletResponse.SC_OK to func.resolveAndCall(params)
        } catch (e : ResultWithCodeException) {
            e.code to e.result
        } catch (e : InvocationTargetException) {
            val cause = e.cause
            if (cause is ResultWithCodeException) {
                cause.code to cause.result
            } else {
                throw e
            }
        }
        object : BaseActionResult(_contentType, code, {
            if (result !is Unit) result?.let { Serialization.serialize(it) } else null
        }) {}
    } catch (e : Exception) {
        logger.error("Failed on ${route()} with $params", e)
        ErrorResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ineternal Server Error")
    }

    override val annotations: List<Annotation> get() = func.annotations
    override val properties: Map<String, Any?> get() = func.parameters.filter { it.kind == KParameter.Kind.VALUE }.associate { it.name!! to params[it.name!!]}
}

private fun KAnnotatedElement.fastRoute(): ResourceDescriptor {
    return ActionContext.tryGet()?.appContext?.dispatcher?.route(this) ?: route()
}

fun KClass<out Resource>.baseLink(): Link = fastRoute().baseLink()
fun Class<out Resource>.baseLink(): Link = kotlinCached.baseLink()
fun KFunction<*>.baseLink() : Link = fastRoute().baseLink()


fun <R: Any> KFunction<R>.href(context: String = contextPath()) = FunctionWrapperResource(this, emptyMap()).href(context)
fun <R: Any> KFunction<R>.href(context: String = contextPath(), params: Map<String, String>) = FunctionWrapperResource(this, params).href(context)
fun <R: Any> KFunction<R>.href(vararg params: Any?) : String {
    val _params = parameters.filter { it.kind == KParameter.Kind.VALUE }.take(params.size).associate { it.name!! to Serialization.serialize(params[it.index])!! }
    return href(context = contextPath(), params = _params)
}

private fun ResourceDescriptor.baseLink() : Link {
    if (route.contains(":")) {
        throw RuntimeException("You can't have base link for the route with URL parameters")
    }

    return (if (allowCrossOrigin == "") {
        "$route?_st=${ActionContext.current().sessionToken()}"
    } else route).link()
}

fun String.link(): Link {
    return DirectLink(appendContext())
}

fun contextPath(): String {
    val request = ActionContext.tryGet()?.request ?: return ""
    return request.getAttribute("CONTEXT_PATH") as? String ?: request.contextPath ?: ""
}

fun HttpServletRequest.setContextPath(path: String) {
    setAttribute("CONTEXT_PATH", path)
}

fun String.appendContext(): String {
    if (startsWith("/") && !startsWith("//")) {
        return contextPath() + this
    }

    return this
}
