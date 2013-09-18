package kara

import kotlin.html.*

public class HtmlPlaceholder<T : HtmlElement>() {
    private var content: T.() -> Unit = { }
    fun invoke(content: T.() -> Unit) {
        this.content = content
    }
    fun T.render() {
        content()
    }
}

public fun <T : HtmlTag> T.insert(placeholder: HtmlPlaceholder<T>): Unit = with(placeholder) { render() }

public open class HtmlTemplateView<T : HtmlTemplate<T>>(val template: T, val build: T.() -> Unit) : ActionResult {
    override fun writeResponse(context: ActionContext) {
        context.response.setContentType("text/html")
        val writer = context.response.getWriter()!!
        val view = this@HtmlTemplateView
        val page = HTML()
        with(template) {
            with(view) { build() }
            with(page) { render(view) }
        }
        writer.write(page.toString())
        writer.flush()
    }
}

public abstract class HtmlTemplate<T : HtmlTemplate<T>>() {
    abstract fun HTML.render(view: HtmlTemplateView<T>)
}
