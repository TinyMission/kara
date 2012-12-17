package kara.server

import javax.servlet.http.*
import kara.app.*
import org.eclipse.jetty.server.*
import org.eclipse.jetty.server.handler.*
import org.eclipse.jetty.server.session.*
import org.apache.log4j.Logger
import kara.config.AppConfig

/** A Runnable responsible for managing a Jetty server instance.
 */
public class JettyRunner(val appRoot : String, val appPackage: String) : AppLoadListener {

    val logger = Logger.getLogger(this.javaClass)!!

    var server : Server? = null
    val handler = Handler()
    val resourceHandler = ResourceHandler()
    val sessionHandler = SessionHandler()

    val appLoader = AppLoader(appRoot, appPackage)

    class Handler() : AbstractHandler() {

        public override fun handle(p0: String?, p1: Request?, p2: HttpServletRequest?, p3: HttpServletResponse?) {

            val app = appLoader.application as Application
            val url = p2?.getRequestURI()!!
            val actionInfo = app.dispatcher.match(p2?.getMethod()!!, url)
            if (actionInfo == null) { // try to match a resource
                resourceHandler.handle(p0, p1, p2, p3)
            }
            else { // let the action handle the request
                try {
                    logger.info("Rendering action ${actionInfo}")
                    actionInfo.exec(p2!!, p3!!)
                }
                catch (ex : Exception) {
                    val out = p3?.getWriter()
                    logger.warn("dispatch error: ${ex.getMessage()}");
                    ex.printStackTrace()
                    out?.print(ex.getMessage())
                    out?.flush()
                }
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
        appLoader.init()
        appLoader.loadApp()
    }

    public fun start() {
        server = Server(8080)

        resourceHandler.setDirectoriesListed(false)
        resourceHandler.setResourceBase("./public")
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
