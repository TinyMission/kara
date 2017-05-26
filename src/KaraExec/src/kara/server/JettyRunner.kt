package kara.server

import kara.Application
import kara.ApplicationConfig
import kara.getDescription
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.AbstractHandler
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.server.session.HashSessionManager
import org.eclipse.jetty.server.session.SessionHandler
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.util.*
import javax.servlet.MultipartConfigElement
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

/** A Runnable responsible for managing a Jetty server instance.
 */
class JettyRunner(val applicationConfig: ApplicationConfig) {
    val logger = LoggerFactory.getLogger(this.javaClass)!!
    lateinit var server: Server
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

        override fun handle(target: String?, baseRequest: Request?, request: HttpServletRequest?, response: HttpServletResponse?) {
            if (baseRequest?.contentType?.contains("multipart/form-data", ignoreCase = true) ?: false) {
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
                            break
                        }
                    }
                }
                if (!baseRequest!!.isHandled) {
                    logger.info("$method -- ${request.requestURL}${if (query != null) "?" + query else ""} -- FAIL")
                }
            }
            catch(ex: Throwable) {
                logger.error("dispatch error: ${ex.message}", ex)
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorDescr(ex, request, request.session!!))
            }
        }
    }

    private fun findApp(request: HttpServletRequest): Application {
        val path = request.requestURI.removePrefix("/").substringBefore("/")

        return apps[path] ?: apps[""] ?: apps[apps.keys.first()]!!
    }

    fun start() {
        logger.info("Starting server...")

        val port = try {
            applicationConfig.port.toInt()
        }
        catch (ex: Exception) {
            throw RuntimeException("${applicationConfig.port} is not a valid port number")
        }
        server = Server(port)

        applicationConfig.publicDirectories.forEach {
            logger.info("Attaching resource handler: $it")
            resourceHandlers.add(ResourceHandler().apply {
                isDirectoriesListed = false
                resourceBase = "./$it"
                welcomeFiles = arrayOf("index.html")
                // Prevent Jetty from locking static files on windows  9
                // see http://www.eclipse.org/jetty/documentation/current/troubleshooting-locked-files-on-windows.html
                if (System.getProperty("os.name").startsWith("Windows", true)) {
                    minMemoryMappedContentLength = -1
                }
            })
        }

        server.handler = SessionHandler().apply {
            sessionManager = HashSessionManager().apply {
                storeDirectory = java.io.File("tmp/sessions")
                httpOnly = true
            }
            handler = Handler()
        }

        server.start()
        logger.info("Server running.")

        if (applicationConfig.tryGet("kara.jetty.dontJoinServer") != "true") {
            server.join()
        }
    }

    fun stop() {
        server.stop()
    }

    fun restart() {
        stop()
        start()
    }
}

fun Throwable.getStackTraceString(): String {
    val os = ByteArrayOutputStream()
    this.printStackTrace(PrintStream(os))
    return os.toString()
}

fun errorDescr(ex: Throwable, request: HttpServletRequest, session: HttpSession): String = buildString {
    append("\nRequest: ${request.requestURI}")
    append("\nSession: ${session.getDescription()}")
    append("\n\nStack Trace:\n")
    append(ex.getStackTraceString())
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
