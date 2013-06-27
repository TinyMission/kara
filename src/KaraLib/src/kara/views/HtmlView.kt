package kara

import kara.internal.*

/** Base class for html views.
 */
abstract class HtmlView(val layout : HtmlLayout? = null) : ActionResult {
    override fun writeResponse(context : ActionContext) : Unit {
        context.response.setContentType("text/html")

        val writer = context.response.getWriter()!!
        if (layout == null) {
            writer.write(this.toString(context.appConfig, context))
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

    fun toString(appConfig : AppConfig, context: ActionContext): String {
        val root = object: HtmlBodyTag(null, "view") {}
        root.render(context)

        val builder = StringBuilder()
        for (child in root.children) {
            child.renderElement(appConfig, builder, "")
        }
        return builder.toString()
    }


    /** Subclasses must implement this to provide the primary html to dispay.
    */
    abstract fun HtmlBodyTag.render(context : ActionContext)
}
