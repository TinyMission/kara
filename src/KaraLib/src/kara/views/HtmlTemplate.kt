package kara

import kotlin.html.*

class HtmlPlaceholder() {
    private var content: HtmlBodyTag.() -> Unit = { }
    fun invoke(content: HtmlBodyTag.() -> Unit) {
        this.content = content
    }
    fun HtmlBodyTag.render() {
        content()
    }
}
fun HtmlBodyTag.render(placeholder: HtmlPlaceholder) = with(placeholder) { render() }
fun <T:HtmlTemplate<T>> T.build(view: HtmlTemplateView<T>) = with(view) { build() }

open class HtmlTemplateView<T:HtmlTemplate<T>>(val template: T? = null, val build: T.() -> Unit) : ActionResult {
    override fun writeResponse(context: ActionContext) {
        context.response.setContentType("text/html")
        val writer = context.response.getWriter()!!
        val t = template
        val view = this@HtmlTemplateView
        if (t == null) {


        }
        else {
            val page = HTML()
            with(t) {
                build(view)
            }
            with(t) {
                with(page) {
                    render(view)
                }
            }
            writer.write(page.toString())
        }
        writer.flush()

    }

}

abstract class HtmlTemplate<T:HtmlTemplate<T>>() {
    abstract fun HTML.render(view: HtmlTemplateView<T>)
}
