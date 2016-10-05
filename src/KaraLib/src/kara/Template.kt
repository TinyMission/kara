package kara

import java.util.*

abstract class Template<in TOuter>() {
    abstract fun TOuter.render()
}

open class TemplatePlaceholder<TOuter, TTemplate>() {
    private var content: (TTemplate.() -> Unit)? = null
    operator fun invoke(content: TTemplate.() -> Unit) {
        this.content = content
    }
    fun TTemplate.render() {
        content?.let {
            it()
        }
    }
    fun isEmpty(): Boolean = content == null
}

open class Placeholder<TOuter>() {
    private var content: (TOuter.(Placeholder<TOuter>) -> Unit)? = null
    var meta : String = ""

    operator fun invoke(meta : String = "", content: TOuter.(Placeholder<TOuter>) -> Unit) {
        this.content = content
        this.meta = meta
    }
    fun TOuter.render() {
        content?.let {
            it(this@Placeholder)
        }
    }
    fun isEmpty(): Boolean = content == null
}

class PlaceholderItem<TOuter>(val index: Int, val collection: List<PlaceholderItem<TOuter>>) : Placeholder<TOuter>() {
    val first: Boolean get() = index == 0
    val last: Boolean get() = index == collection.size
}

open class Placeholders<TOuter, TInner>() {
    private var items = ArrayList<PlaceholderItem<TInner>>()
    operator fun invoke(meta : String = "", content: TInner.(Placeholder<TInner>) -> Unit = {}) {
        val placeholder = PlaceholderItem(items.size, items)
        placeholder(meta, content)
        items.add(placeholder)
    }

    fun isEmpty() : Boolean = items.size == 0
    fun TOuter.render(render: TOuter.(PlaceholderItem<TInner>) -> Unit) {
        for (item in items) {
            render(item)
        }
    }
}

fun <TOuter, TInner> TOuter.each(items: Placeholders<TOuter, TInner>, itemTemplate: TOuter.(PlaceholderItem<TInner>) -> Unit): Unit {
    with(items) { render(itemTemplate) }
}

fun <TOuter> TOuter.insert(placeholder: Placeholder<TOuter>): Unit = with(placeholder) { render() }

fun <TTemplate : Template<TOuter>, TOuter> TOuter.insert(template: TTemplate, placeholder: TemplatePlaceholder<TOuter, TTemplate>) {
    with(placeholder) { template.render() }
    with(template) { render() }

}

fun <TOuter, TTemplate : Template<TOuter>> TOuter.insert(template: TTemplate, build: TTemplate.() -> Unit) {
    with(template) {
        build()
        render()
    }
}

