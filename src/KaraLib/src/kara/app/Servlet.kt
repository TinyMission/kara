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
            javaClass.getClassLoader()!!.getResource("config/$it")
        }

        val loader = AppLoader(config)
        loader.loadApp()
        return loader.application!!
    }

    override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
        try {
            app.dispatcher.dispatch(req!!, resp!!)
        }
        catch (ex : Exception) {
            val out = resp?.getWriter()
            out?.println("Error handling request $req:")
            out?.println(ex.getMessage())
            out?.println(ex.printStackTrace())
        }
    }

}
