package kara.controllers

import javax.servlet.http.HttpServletResponse
import kara.views.ActionContext

/** Base class for objects that are returned from actions.
 */
trait ActionResult {

    /** Subclasses must implement this to write the action result to the HTTP response.
    */
    fun writeResponse(context : ActionContext) : Unit
}


/** Simple text result.
 */
open class TextResult(val text : String) : ActionResult {
    override fun writeResponse(context : ActionContext) {
        val out = context.response.getWriter()
        out?.print(text)
        out?.flush()
    }
}


/** Redirect result.
*/
open class RedirectResult(val url : String) : ActionResult {
    override fun writeResponse(context : ActionContext) {
        context.response.sendRedirect(url)
    }
}