package kara

import javax.servlet.http.*
import kara.internal.*


fun HttpSession.getDescription() : String {
    return this.getAttributeNames()!!.iterator().map { it ->
        "${it}: ${this.getAttribute(it)}"
    }.toArrayList().join(", ")
}


/** This contains information about the current rendering action.
 * An action context is provided by the dispatcher to the action result when it's rendered.
 */
class ActionContext(val appConfig : AppConfig,
                    val request : HttpServletRequest,
                    val response : HttpServletResponse,
                    val params : RouteParameters) {
    public val session : HttpSession = request.getSession(true)!!
    public val startedAt : Long = System.currentTimeMillis()

    fun redirect(url : String) : ActionResult {
        return RedirectResult(url)
    }
}
