package kara.server

import kara.Application
import kara.ApplicationConfig
import kara.getDescription
import org.apache.log4j.Logger
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.AbstractHandler
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.server.session.HashSessionManager
import org.eclipse.jetty.server.session.SessionHandler
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.util.*
import javax.servlet.MultipartConfigElement
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

/** A Runnable responsible for managing a Jetty server instance.
 */
public class JettyRunner(val applicationConfig: ApplicationConfig) {
    val logger = Logger.getLogger(this.javaClass)!!
    var server: Server? = null
    val resourceHandlers = ArrayList<ResourceHandler>()

    val apps = HashMap<String, Application>()

    init {
        val appContexts = applicationConfig.tryGet("kara.apps")?.split(',')?.map{it.trim()}?.filterNot {it.startsWith('-')} ?: listOf("")

        for (ctx in appContexts) {
            apps[ctx] = Application.load(applicationConfig, ctx)
        }
    }

    inner class Handler() : AbstractHandler() {
        val CONFIG = MultipartConfigElement(System.getProperty("java.io.tmpdir"))

        public override fun handle(target: String?, baseRequest: Request?, request: HttpServletRequest?, response: HttpServletResponse?) {
            if (baseRequest?.contentType?.let { it.contains("multipart/form-data", ignoreCase = true) } ?: false) {
                baseRequest?.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, CONFIG)
            }

            response!!.characterEncoding = "UTF-8"
            val query = request!!.queryString
            val method = request.method
            try {
                val app = findApp(request)
                val appContext = app.appContext
                val targ = if (appContext.isNotBlank()) target?.removePrefix("/$appContext") else target

                if (app.context.dispatch(request.appendContext(appContext), response)) {
                    baseRequest!!.isHandled = true
                    logger.info("$method -- ${request.requestURL}${if (query != null) "?" + query else ""} -- OK[${response.status}]")
                }
                else {
                    for (resourceHandler in resourceHandlers) {
                        resourceHandler.handle(targ, baseRequest, request.removeContext(appContext), response)
                        if (baseRequest!!.isHandled) {
                            logger.info("$method -- ${request.requestURL}${if (query != null) "?" + query else ""} -- OK @${resourceHandler.resourceBase}")
                            break;
                        }
                    }
                }
                if (!baseRequest!!.isHandled) {
                    logger.info("$method -- ${request.requestURL}${if (query != null) "?" + query else ""} -- FAIL")
                }
            }
            catch(ex: Throwable) {
                logger.error("dispatch error: ${ex.getMessage()}", ex);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorDescr(ex, request, request.session!!))
            }
        }
    }

    private fun findApp(request: HttpServletRequest): Application {
        val path = request.requestURI.removePrefix("/").substringBefore("/")

        return apps[path] ?: apps[""] ?: apps[apps.keySet().first()]!!
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
            logger.info("Attaching resource handler: $it")
            val resourceHandler = ResourceHandler()
            resourceHandler.isDirectoriesListed = false
            resourceHandler.resourceBase = "./$it"
            resourceHandler.welcomeFiles = arrayOf("index.html")
            resourceHandlers.add(resourceHandler)
        }

        val sessionHandler = SessionHandler()
        val sessionManager = HashSessionManager()
        sessionManager.storeDirectory = java.io.File("tmp/sessions")
        sessionHandler.sessionManager = sessionManager
        sessionHandler.handler = Handler()
        server?.handler = sessionHandler

        server?.start()
        logger.info("Server running.")

        if (applicationConfig.tryGet("kara.jetty.dontJoinServer") != "true") {
            server?.join()
        }
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
        append("\nRequest: ${request.requestURI}")
        append("\nSession: ${session.getDescription()}")
        append("\n\nStack Trace:\n")
        append(ex.getStackTraceString())

        toString()
    }
}

private fun HttpServletRequest.appendContext(ctx: String) = when {
    ctx.isBlank() -> this
    else -> ContextReqest(this, ctx)
}

private fun HttpServletRequest.removeContext(ctx: String) = when {
    ctx.isBlank() -> this
    else -> ContextRemovedReqest(this, ctx)
}

private class ContextReqest(val w: HttpServletRequest, val context: String) : HttpServletRequest by w {
    override fun getContextPath(): String? = w.contextPath.orEmpty() + "/" + context
}

private class ContextRemovedReqest(val w: HttpServletRequest, val context: String) : HttpServletRequest by w {
    override fun getPathInfo(): String? = w.pathInfo?.removePrefix("/$context")
}
