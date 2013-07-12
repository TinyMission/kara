package kara

import javax.servlet.http.HttpServletResponse
import java.io.IOException

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

open class ErrorResult(val code : Int, val msg : String?) : ActionResult {
    override fun writeResponse(context : ActionContext) {
        context.response.sendError(code, msg)
    }
}

open class RequestAuthentication(val realm : String) : ErrorResult(401, "Not authorized") {
    override fun writeResponse(context : ActionContext) {
        context.response.addHeader("WWW-Authenticate", "Basic realm=\"$realm\"")
        context.response.sendError(code, msg)
    }
}

fun ActionResult.tryWriteResponse(context: ActionContext) {
    try {
        writeResponse(context)
    }
    catch(ex: IOException) {
        // All kinds of EOFs and Broken Pipes can be safely ignored
    }
    catch(ex: Exception) {
        println(ex.printStackTrace())
        context.response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage())
    }
}
