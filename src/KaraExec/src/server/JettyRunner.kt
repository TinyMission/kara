package kara.server

import javax.servlet.http.*
import org.eclipse.jetty.server.*
import org.eclipse.jetty.server.handler.*
import org.eclipse.jetty.server.session.*
import org.apache.log4j.Logger
import kara.internal.*
import kara.app.*
import kara.AppConfig
import kara.Application

/** A Runnable responsible for managing a Jetty server instance.
 */
public class JettyRunner(val appConfig: AppConfig) : AppLoadListener {

    val logger = Logger.getLogger(this.javaClass)!!

    var server : Server? = null
    val handler = Handler()
    val resourceHandler = ResourceHandler()
    val sessionHandler = SessionHandler()

    val appLoader = AppLoader(appConfig)

    inner class Handler() : AbstractHandler() {

        public override fun handle(p0: String?, p1: Request?, p2: HttpServletRequest?, p3: HttpServletResponse?) {

            val app = appLoader.application as Application
            p3!!.setCharacterEncoding("UTF-8")

            try {
                if (app.dispatch(p2!!, p3)) {
                    p1!!.setHandled(true)
                }
                else {
                    resourceHandler.handle(p0, p1, p2, p3)
                }
            }
            catch(ex: Throwable) {
                logger.warn("dispatch error: ${ex.getMessage()}");
                ex.printStackTrace()
                val out = p3.getWriter()
                out?.print(ex.getMessage())
                out?.flush()
            }
        }
    }


    /** Mark the server for restarting if the target jar changes.
    */
    override fun onAppLoaded(application : Application) {
        logger.info("Application ${application.javaClass} loaded into the jetty runner");
    }

    public fun init() {
        val sessionManager = HashSessionManager()
        //println("setting session dir: ${AppConfig.current.absSessionDir}")
        sessionManager.setStoreDirectory(java.io.File("tmp/sessions"))
        sessionHandler.setSessionManager(sessionManager)
        sessionHandler.setHandler(handler)


        appLoader.addListener(this)
        appLoader.loadApp()
    }

    public fun start() {
        var port : Int
        try {
            port = appConfig.port.toInt()
        }
        catch (ex : Exception) {
            throw RuntimeException("${appConfig.port} is not a valid port number")
        }
        server = Server(port)

        resourceHandler.setDirectoriesListed(false)
        resourceHandler.setResourceBase("./${appConfig.publicDir}")
        resourceHandler.setWelcomeFiles(array("index.html"))

        server?.setHandler(sessionHandler)

        server?.start()
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
