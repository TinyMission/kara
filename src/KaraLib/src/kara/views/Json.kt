package kara

import javax.servlet.http.HttpServletResponse
import org.codehaus.jackson.map.ObjectMapper

/** JSON Action Result.
 */
class Json(val obj : Any) : ActionResult {
    override fun writeResponse(context : ActionContext) {
        val out = context.response.getWriter()
        val mapper = ObjectMapper()
        mapper.writeValue(out, obj)
        out?.flush()
    }

}
