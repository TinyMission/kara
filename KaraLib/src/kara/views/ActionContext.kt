package kara.views

import javax.servlet.http.*
import kara.controllers.RouteParams
import kotlin.nullable.map
import kara.util.*


fun HttpSession.getDescription() : String {
    return this.getAttributeNames()!!.iterator().map { it ->
        "${it}: ${this.getAttribute(it)}"
    }.toArrayList().join(", ")
}


/** This contains information about the current rendering action.
 * An action context is provided by the dispatcher to the action result when it's rendered.
 */
class ActionContext(val request : HttpServletRequest, val response : HttpServletResponse, val params : RouteParams) {
    public val session : HttpSession = request.getSession(true)!!
}