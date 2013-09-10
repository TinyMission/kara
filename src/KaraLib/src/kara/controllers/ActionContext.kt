package kara

import javax.servlet.http.*
import kara.internal.*
import kotlin.html.Link


fun HttpSession.getDescription() : String {
    return this.getAttributeNames()!!.iterator().map { it ->
        "${it}: ${this.getAttribute(it)}"
    }.toArrayList().join(", ")
}


/** This contains information about the current rendering action.
 * An action context is provided by the dispatcher to the action result when it's rendered.
 */
class ActionContext(val app: Application,
                    val request : HttpServletRequest,
                    val response : HttpServletResponse,
                    val params : RouteParameters) {
    public val session : HttpSession = request.getSession(true)!!
    public val startedAt : Long = System.currentTimeMillis()

    fun redirect(link: Link): ActionResult {
        return RedirectResult(link.href())
    }

    fun redirect(url : String) : ActionResult {
        return RedirectResult(url)
    }

    class object {
        val contexts = ThreadLocal<ActionContext?>()

        public fun current(): ActionContext {
            val context = tryGet()
            if (context == null)
                throw ContextException("Operation is not in context of an action, ActionContext not set.")
            return context
        }

        fun tryGet(): ActionContext? {
            return contexts.get()
        }
    }
}

class ContextException(msg : String) : Exception(msg) {}

public fun <T> ActionContext.withContext(body: () -> T): T {
    try {
        ActionContext.contexts.set(this)
        return body()
    }
    finally {
        ActionContext.contexts.set(null)
    }
}

