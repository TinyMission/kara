package kara

import kotlinx.html.HTML
import kotlinx.html.HtmlBodyTag
import javax.servlet.http.HttpServletResponse.SC_OK

/** Base class for html views.
 */
abstract class HtmlView(val layout: HtmlLayout? = null) : BaseActionResult("text/html", SC_OK, {
    val mainView = this as HtmlView
    layout?.let {
        HTML().apply {
            with(it) {
                render(mainView)
            }
        }.toString()
    } ?: mainView.renderWithoutLayout()
})   {
    fun renderWithoutLayout() = buildString {
        val root = object: HtmlBodyTag(null, "view") {}
        root.render()
        for (child in root.children) {
            child.renderElement(this, "")
        }
    }

    /** Subclasses must implement this to provide the primary html to dispay.
     */
    abstract fun HtmlBodyTag.render()
}

fun HtmlBodyTag.renderView(view: HtmlView) : Unit = with(view) { render() }

