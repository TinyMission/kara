package kara.controllers

import javax.servlet.http.*
import kara.views.HtmlLayout

/** Base class for all controllers.
 * This should be subclassed to provide controller classes for your application.
 */
open class BaseController(var layout : HtmlLayout? = null) {

    public var request : HttpServletRequest? = null
    public var response : HttpServletResponse? = null
    public var params : RouteParams = RouteParams()

    public val session : HttpSession
        get() = request?.getSession()!!

    public var root : String = "/" + this.javaClass.getSimpleName().replace("Controller", "").toLowerCase() + "/"

    fun beforeRequest(request: HttpServletRequest, response : HttpServletResponse, params : RouteParams) {
        this.request = request
        this.response = response
        this.params = params
    }

    /** Redirects to the given url.
    */
    fun redirect(url : String) : ActionResult {
        return RedirectResult(url)
    }

}