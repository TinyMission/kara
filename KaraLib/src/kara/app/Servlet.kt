package kara.servlet

import javax.servlet.http.*
import java.io.IOException
import org.reflections.Reflections
import kara.controllers.Dispatcher
import kara.config.AppConfig
import kara.app.Application

open class Servlet(val app: Application, val appConfig : AppConfig) : HttpServlet() {
    fun doGet(request: HttpServletRequest, response : HttpServletResponse) {
        if (appConfig.isDevelopment()) {
            app.dispatcher.reset()
        }

        try {

            app.dispatcher.dispatch(appConfig, request, response)
        }
        catch (ex : Exception) {
            val out = response.getWriter()
            out?.println("Error handling request $request:")
            out?.println(ex.getMessage())
            out?.println(ex.printStackTrace())
        }
    }

}
