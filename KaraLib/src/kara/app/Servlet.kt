package kara.servlet

import javax.servlet.http.*
import java.io.IOException
import org.reflections.Reflections
import kara.controllers.Dispatcher
import kara.config.AppConfig

open class Servlet(val appConfig : AppConfig) : HttpServlet() {

    var dispatcher : Dispatcher? = null

    fun initDispatcher() {
        dispatcher = Dispatcher()
        dispatcher?.initWithReflection(appConfig)
    }

    public override fun init() {
        super<HttpServlet>.init()
        initDispatcher()
    }

    fun doGet(request: HttpServletRequest, response : HttpServletResponse) {
        if (appConfig.isDevelopment()) {
            initDispatcher()
        }
        try {

            dispatcher?.dispatch(appConfig, request, response)
        }
        catch (ex : Exception) {
            val out = response.getWriter()
            out?.println("Error handling request $request:")
            out?.println(ex.getMessage())
            out?.println(ex.printStackTrace())
        }
    }

}