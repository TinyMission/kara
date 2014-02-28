package kara

import kotlin.html.*
import java.util.ArrayList
import javax.servlet.http.HttpServletResponse

open public class TemplatePlaceholder<TOuter, TTemplate>() {
    private var content: (TTemplate.() -> Unit)? = null
    fun invoke(content: TTemplate.() -> Unit) {
        this.content = content
    }
    fun TTemplate.render() {
        content?.let {it()}
    }
    fun isEmpty(): Boolean = content == null
}

open public class Placeholder<TOuter>() {
    private var content: (TOuter.() -> Unit)? = null
    fun invoke(content: TOuter.() -> Unit) {
        this.content = content
    }
    fun TOuter.render() {
        content?.let {it()}
    }
    fun isEmpty(): Boolean = content == null
}

public class PlaceholderItem<TOuter>(val index : Int, val collection : List<PlaceholderItem<TOuter>>) : Placeholder<TOuter>() {
    val first : Boolean get() = index == 0
    val last : Boolean get() = index == collection.size
}

open public class Placeholders<TOuter>() {
    private var items = ArrayList<PlaceholderItem<TOuter>>()
    fun invoke(content: TOuter.() -> Unit) {
        val placeholder = PlaceholderItem<TOuter>(items.size, items)
        placeholder(content)
        items.add(placeholder)
    }

    fun TOuter.render(render: TOuter.(PlaceholderItem<TOuter>) -> Unit) {
        for (item in items) {
            render(item)
        }
    }
}

public fun <TOuter> TOuter.each(items: Placeholders<TOuter>, itemTemplate: TOuter.(PlaceholderItem<TOuter>) -> Unit): Unit = with(items) { render(itemTemplate) }

public fun <TOuter> TOuter.insert(placeholder: Placeholder<TOuter>): Unit = with(placeholder) { render() }

public fun <TTemplate, TOuter> TOuter.insert(template: TTemplate, placeholder: TemplatePlaceholder<TOuter, TTemplate>)
        where TTemplate : HtmlTemplate<TTemplate, TOuter> {
    with(placeholder) { template.render() }
    with(template) { render() }

}

public open class HtmlTemplateView<Template : HtmlTemplate<Template, HTML>>(val template: Template, val build: Template.() -> Unit) : ActionResult {
    override fun writeResponse(context: ActionContext) {
        writeResponse(context.response)
    }

    fun writeResponse(response: HttpServletResponse) {
        response.setContentType("text/html")
        val writer = response.getWriter()!!
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
