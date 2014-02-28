package kara.server

import kara.*
import kara.internal.*
import javax.servlet.http.*
import org.eclipse.jetty.server.*
import org.eclipse.jetty.server.handler.*
import org.eclipse.jetty.server.session.*
import org.apache.log4j.Logger
import java.util.ArrayList
import java.io.ByteArrayOutputStream
import java.io.PrintStream

/** A Runnable responsible for managing a Jetty server instance.
 */
public class JettyRunner(val applicationConfig: ApplicationConfig) {
    val logger = Logger.getLogger(this.javaClass)!!
    var server: Server? = null
    val resourceHandlers = ArrayList<ResourceHandler>()

    val application: Application;
    {
        val logger = Logger.getLogger(this.javaClass)!!
        val applicationLoader = ApplicationLoader(applicationConfig)
        applicationLoader.loaded { logger.info("Application ${it.javaClass} loaded into the jetty runner") }
        application = applicationLoader.load()
    }

    inner class Handler() : AbstractHandler() {
        public override fun handle(target: String?, baseRequest: Request?, request: HttpServletRequest?, response: HttpServletResponse?) {
            response!!.setCharacterEncoding("UTF-8")
            val query = request!!.getQueryString()
            val method = request.getMethod()
            try {
                if (application.context.dispatch(request, response)) {
                    baseRequest!!.setHandled(true)
                    logger.info("$method -- ${request.getRequestURL()}${if (query != null) "?" + query else ""} -- OK")
                }
                else {
                    for (resourceHandler in resourceHandlers) {
                        resourceHandler.handle(target, baseRequest, request, response)
                        if (baseRequest!!.isHandled()) {
                            logger.info("$method -- ${request.getRequestURL()}${if (query != null) "?" + query else ""} -- OK @${resourceHandler.getResourceBase()}")
                            break;
                        }
                    }
                }
                if (!baseRequest!!.isHandled()) {
                    logger.info("$method -- ${request.getRequestURL()}${if (query != null) "?" + query else ""} -- FAIL")
                }
            }
            catch(ex: Throwable) {
                logger.error("dispatch error: ${ex.getMessage()}");
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorDescr(ex, request, request.getSession()!!))
            }
        }
    }

    public fun start() {
        logger.info("Starting server...")

        var port: Int
        try {
            port = applicationConfig.port.toInt()
        }
        catch (ex: Exception) {
            throw RuntimeException("${applicationConfig.port} is not a valid port number")
        }
        server = Server(port)

        applicationConfig.publicDirectories.forEach {
            logger.info("Attaching resource handler: ${it}")
            val resourceHandler = ResourceHandler()
            resourceHandler.setDirectoriesListed(false)
            resourceHandler.setResourceBase("./${it}")
            resourceHandler.setWelcomeFiles(array("index.html"))
            resourceHandlers.add(resourceHandler)
        }

        val sessionHandler = SessionHandler()
        val sessionManager = HashSessionManager()
        sessionManager.setStoreDirectory(java.io.File("tmp/sessions"))
        sessionHandler.setSessionManager(sessionManager)
        sessionHandler.setHandler(Handler())
        server?.setHandler(sessionHandler)

        server?.start()
        logger.info("Server running.")
        server?.join()
    }

    public fun stop() {
        if (server != null) {
            server?.stop()
            server = null
        }
    }

    public fun restart() {
        this.stop()
        this.start()
    }

}


fun Throwable.getStackTraceString(): String {
    val os = ByteArrayOutputStream()
    this.printStackTrace(PrintStream(os))
    return os.toString()
}

fun errorDescr(ex: Throwable, request: HttpServletRequest, session: HttpSession): String {
    return with (StringBuilder()) {
        append("\nRequest: ${request.getRequestURI()}")
        append("\nSession: ${session.getDescription()}")
        append("\n\nStack Trace:\n")
        append(ex.getStackTraceString())

        toString()
    }
}
