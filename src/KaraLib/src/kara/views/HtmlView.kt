package kara

import kara.internal.*

/** Base class for html views.
 */
abstract class HtmlView(var layout : HtmlLayout? = null) : BodyTag("view", false), ActionResult {


    override fun writeResponse(context : ActionContext) : Unit {
        context.response.setContentType("text/html")

        val writer = context.response.getWriter()!!
        tagStack = TagStack(this)
        if (layout == null) {
            render(context)
            writer.write(this.toString(context.appConfig)!!)
        }
        else {
            layout?.children?.clear()
            layout?.render(context, this)
            writer.write(layout?.toString(context.appConfig)!!)
        }
        writer.flush()
    }

    override fun toString(appConfig : AppConfig): String? {
        val builder = StringBuilder()
        for (child in children) {
            child.render(appConfig, builder, "")
        }
        return builder.toString()
    }


    /** Subclasses must implement this to provide the primary html to dispay.
    */
    abstract fun render(context : ActionContext)
}
