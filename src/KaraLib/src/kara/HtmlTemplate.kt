package kara

import kotlin.html.*

public class TemplatePlaceholder<TOuter, TTemplate>() {
    private var content: TTemplate.() -> Unit = { }
    fun invoke(content: TTemplate.() -> Unit) { this.content = content }
    fun TTemplate.render() { content() }
}

public class Placeholder<TOuter>() {
    private var content: TOuter.() -> Unit = { }
    fun invoke(content: TOuter.() -> Unit) { this.content = content }
    fun TOuter.render() { content() }
}

trait TemplateBuilder<Template> { fun create(): Template }

public fun <TOuter> TOuter.insert(placeholder: Placeholder<TOuter>): Unit = with(placeholder) { render() }

fun <TTemplate, TOuter> TOuter.insert(template : TTemplate, placeholder: TemplatePlaceholder<TOuter, TTemplate>)
        where TTemplate : HtmlTemplate<TTemplate, TOuter> {
    with(placeholder) { template.render() }
    with(template) { render() }

}

public open class HtmlTemplateView<Template : HtmlTemplate<Template, HTML>>(val template: Template, val build: Template.() -> Unit) : ActionResult {
    override fun writeResponse(context: ActionContext) {
        context.response.setContentType("text/html")
        val writer = context.response.getWriter()!!
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

public abstract class HtmlTemplate<T : HtmlTemplate<T, TOuter>, TOuter>() {
    abstract fun TOuter.render()
}
