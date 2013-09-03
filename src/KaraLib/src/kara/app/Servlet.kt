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
        val config:AppConfig = AppConfig(getInitParameter("environment") ?: "development")

        val loader = AppLoader(config)
        loader.loadApp()
        return loader.application!!
    }


    public override fun destroy() {
        _app?.shutDown()
    }

    protected override fun service(req: HttpServletRequest?, resp: HttpServletResponse?) {
        dispatch(req!!, resp!!)
    }

    private fun dispatch(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.setCharacterEncoding("UTF-8")
        req.setCharacterEncoding("UTF-8")

        try {
            if (!app.dispatch(req, resp)) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND)
            }
        }
        catch (ex : Throwable) {
            println(ex.printStackTrace())
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage())
        }
    }

}
