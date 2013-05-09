package kara.internal

import javax.servlet.http.*
import java.io.IOException
import kara.*

open class Servlet() : HttpServlet() {
    val app: Application = run {
        throw RuntimeException("Not implemented yet!")
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
