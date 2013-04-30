package kara

import kara.internal.*

/** Base class for html views.
 */
abstract class HtmlView(val layout : HtmlLayout? = null) : HtmlBodyTag(null, "view"), ActionResult {
    override fun writeResponse(context : ActionContext) : Unit {
        context.response.setContentType("text/html")

        val writer = context.response.getWriter()!!
        if (layout == null) {
            render(context)
            writer.write(this.toString(context.appConfig)!!)
        }
        else {
            val page = HTML()
            with(layout!!) {
                page.render(context, this@HtmlView)
            }
            writer.write(page.toString(context.appConfig)!!)
        }
        writer.flush()
    }

    fun <T:Any> with(t: T, body: T.() -> Unit) {
        t.body()
    }

    override fun toString(appConfig : AppConfig): String {
        val builder = StringBuilder()
        for (child in children) {
            child.renderElement(appConfig, builder, "")
        }
        return builder.toString()
    }


    /** Subclasses must implement this to provide the primary html to dispay.
    */
    abstract fun render(context : ActionContext)
}
