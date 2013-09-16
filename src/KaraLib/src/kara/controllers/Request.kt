package kara

import java.net.URL
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest
import java.lang.reflect.Modifier
import kara.internal.*
import java.util.HashSet
import java.util.LinkedHashSet
import kotlin.html.*

public open class Request(private val handler: ActionContext.() -> ActionResult) : Link {
    fun handle(context: ActionContext): ActionResult = context.handler()

    override fun href() = toExternalForm()

    public fun toExternalForm(): String {
        val route = ActionContext.tryGet()?.app?.dispatcher?.route(javaClass) ?: javaClass.route().first

        val answer = StringBuilder()

        val properties = LinkedHashSet(properties())
        val components = route.toRouteComponents().map({
            when (it) {
                is StringRouteComponent -> it.componentText
                is OptionalParamRouteComponent -> {
                    properties.remove(it.name)
                    val value = propertyValue(it.name)
                    if (value == null) null else value.toString()
                }
                is ParamRouteComponent -> {
                    properties.remove(it.name)

                    // TODO: introduce serializers similar to deserializers in appconfig
                    "${propertyValue(it.name)}"
                }
                is WildcardRouteComponent -> throw RuntimeException("Routes with wildcards aren't supported")
                else -> throw RuntimeException("Unknown route component $it of class ${it.javaClass.getName()}")
            }
        })

        answer.append(components.filterNotNull().join("/"))

        if (answer.length() == 0) answer.append("/")

        val nonEmptyProperties = properties filter { propertyValue(it) != null }
        if (nonEmptyProperties.count() > 0) {
            answer.append("?")
            answer.append(nonEmptyProperties map { "$it=${propertyValue(it)}" } join("&"))
        }

        return answer.toString()
    }
}
