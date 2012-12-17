package kara.servlet

import javax.servlet.http.*
import java.io.IOException
import org.reflections.Reflections
import kara.controllers.Dispatcher
import kara.config.AppConfig

open class Servlet() : HttpServlet() {

    var dispatcher : Dispatcher? = null

    fun initDispatcher() {
        dispatcher = Dispatcher(this.javaClass.getPackage()?.getName() as String)
        dispatcher?.initWithReflection()
    }

    public override fun init() {
        super<HttpServlet>.init()
        initDispatcher()
    }

    fun doGet(request: HttpServletRequest, response : HttpServletResponse) {
        if (AppConfig.current.isDevelopment()) {
            initDispatcher()
        }
        try {

            dispatcher?.dispatch(request, response)
        }
        catch (ex : Exception) {
            val out = response.getWriter()
            out?.println("Error handling request $request:")
            out?.println(ex.getMessage())
            out?.println(ex.printStackTrace())
        }
    }

}