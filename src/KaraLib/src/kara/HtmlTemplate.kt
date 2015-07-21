package kara

import kotlin.html.*
import java.util.ArrayList
import javax.servlet.http.HttpServletResponse

public open class HtmlTemplateView<T : Template<HTML>>(val template: T, val build: T.() -> Unit) : ActionResult {
    override fun writeResponse(context: ActionContext) {
        writeResponse(context.response)
    }

    fun writeResponse(response: HttpServletResponse) {
        response.contentType = "text/html"
        val writer = response.writer!!
        val view = this@HtmlTemplateView
        val page = HTML()
        with(template) {
            with(view) { build() }
            with(page) { render() }
        }
        writer.write(page.toString())
        writer.flush()
    }
}
