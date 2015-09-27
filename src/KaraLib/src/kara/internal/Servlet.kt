package kara.internal

import kara.Application
import kara.ApplicationConfig
import org.apache.log4j.Logger
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

public open class Servlet() : HttpServlet() {
    val logger = Logger.getLogger(Servlet::class.java)!!

    val application: Application by lazy {
        val servletContext = servletContext!!
        val config: ApplicationConfig = ApplicationConfig.loadFrom(servletContext.getInitParameter("kara.config") ?: error("kara.config context parameter is required."))

        for (name in servletContext.initParameterNames) {
            config[name] = servletContext.getInitParameter(name)!!
        }

        Application.load(config,"")
    }


    override fun init() {
        super.init()
        application.config // Just to make sure application is eagerly loaded when servlet is initialized.
    }

    public override fun destroy() {
        application.shutDown()
    }

    protected override fun service(req: HttpServletRequest?, resp: HttpServletResponse?) {
        dispatch(req!!, resp!!)
    }

    private fun dispatch(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.characterEncoding = "UTF-8"
        req.characterEncoding = "UTF-8"

        try {
            val query = req.queryString
            if (!application.context.dispatch(req, resp)) {
                logger.trace("${req.method} -- ${req.requestURL}${if (query != null) "?" + query else ""} -- FAILED")
                resp.sendError(HttpServletResponse.SC_NOT_FOUND)
            } else {
                logger.trace("${req.method} -- ${req.requestURL}${if (query != null) "?" + query else ""} -- OK")
            }
        }
        catch (ex: Throwable) {
            logger.error("Error processing request: ${req.requestURI}, User agent: ${req.getHeader("User-Agent")}", ex)
            if (!resp.isCommitted) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage())
            }
        }
    }

}
