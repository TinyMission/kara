package kara.views

import kara.controllers.ActionResult

/** Base class for html views.
 */
abstract class HtmlView(var layout : HtmlLayout? = null) : BodyTag("view", false), ActionResult {


    override fun writeResponse(context : ActionContext) : Unit {
        val writer = context.response.getWriter()!!
        tagStack = TagStack(this)
        if (layout == null) {
            render(context)
            writer.write(this.toString()!!)
        }
        else {
            layout?.children?.clear()
            layout?.render(context, this)
            writer.write(layout?.toString()!!)
        }
        writer.flush()
    }

    override fun toString(): String? {
        val builder = StringBuilder()
        for (val child in children) {
            child.render(builder, "")
        }
        return builder.toString()
    }


    /** Subclasses must implement this to provide the primary html to dispay.
    */
    abstract fun render(context : ActionContext)
}
