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
        val currents = ThreadLocal<ActionContext?>()

        public fun current(): ActionContext? {
            return currents.get()
        }
    }
}

public fun <T> ActionContext.withContext(body: () -> T): T {
    try {
        ActionContext.currents.set(this)
        return body()
    }
    finally {
        ActionContext.currents.set(null)
    }
}

