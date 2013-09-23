package kara.internal

import javax.servlet.http.*
import java.io.IOException
import kara.*
import javax.servlet.ServletConfig

open class Servlet() : HttpServlet() {
    private var _application: Application? = null

    val application: Application get() = _application ?: run {
        _application = loadApp()
        _application!!
    }

    private fun loadApp(): Application {
        val config: ApplicationConfig = ApplicationConfig(getInitParameter("environment") ?: "development")
        getInitParameter("host")?.let { config.set("host", it) }
        getInitParameter("port")?.let { config.set("port", it) }

        return ApplicationLoader(config).load()
    }

    public override fun destroy() {
        _application?.shutDown()
    }

    protected override fun service(req: HttpServletRequest?, resp: HttpServletResponse?) {
        dispatch(req!!, resp!!)
    }

    private fun dispatch(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.setCharacterEncoding("UTF-8")
        req.setCharacterEncoding("UTF-8")

        try {
            if (!application.context.dispatch(req, resp)) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND)
            }
        }
        catch (ex: Throwable) {
            println(ex.printStackTrace())
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage())
        }
    }

}
