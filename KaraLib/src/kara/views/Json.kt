package kara.views

import kara.controllers.ActionResult
import javax.servlet.http.HttpServletResponse
import com.google.gson.Gson

/** JSON Action Result.
 */
class Json(val obj : Any) : ActionResult {
    override fun writeResponse(context : ActionContext) {
        val out = context.response.getWriter()
        val gson = Gson()
        gson.toJson(obj, out)
        out?.flush()
    }

}
