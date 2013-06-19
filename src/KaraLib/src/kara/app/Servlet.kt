package kara.internal

import javax.servlet.http.*
import java.io.IOException
import kara.*
import javax.servlet.ServletConfig
import kara.app.AppLoader

open class Servlet() : HttpServlet() {
    private var _app: Application? = null

    val app: Application get() {
        return _app ?: run {
            _app = loadApp()
            _app!!
        }
    }

    private fun loadApp(): Application {
        val config:AppConfig = AppConfig() {
            javaClass.getResource("/config/$it")
        }

        val loader = AppLoader(config)
        loader.loadApp()
        return loader.application!!
    }


    protected override fun service(req: HttpServletRequest?, resp: HttpServletResponse?) {
        dispatch(req!!, resp!!)
    }

    private fun dispatch(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.setCharacterEncoding("UTF-8")
        req.setCharacterEncoding("UTF-8")

        try {
            app.dispatcher.dispatch(req, resp)
        }
        catch (ex : Exception) {
            println(ex.printStackTrace())
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage())
        }
    }

}
