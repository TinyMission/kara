package kotlin.html.bootstrap

import kotlin.html.*
import java.util.ArrayList
import kara.link

inline fun HtmlBodyTag.pills(body: UL.()->Unit): Unit = ul("nav nav-pills", contents = body)
inline fun HtmlBodyTag.content(body: DIV.()->Unit): Unit = div("tab-content", contents = body)
public fun HtmlBodyTag.pane(name: String, active: Boolean = false, body: DIV.()->Unit): Unit = div {
    addClass("tab-pane")
    if (active)
        addClass("active")
    this.id = name
    body()
}

public fun HtmlBodyTag.tabs(body: UL.()->Unit): Unit = ul("nav nav-tabs", contents = body)

public class PagesBuilder() {
    class Item(val id: String, val title: String, val content: HtmlBodyTag.() -> Unit)

    val items = ArrayList<Item>()

    public fun item(name: String, title: String, content : HtmlBodyTag.() -> Unit) {
        items.add(Item(name, title, content))
    }
}

public fun HtmlBodyTag.tabs(activeName: String, body: PagesBuilder.() -> Unit) {
    val builder = PagesBuilder()
    builder.body()

    tabs {
        for (item in builder.items) {
            item(activeName == item.id) {
                a {
                    attribute("data-toggle", "tab")
                    href = "#${item.id}".link()
                    +item.title
                }
            }
        }
    }

    content {
        for (item in builder.items) {
            pane(item.id, activeName == item.id) {
                item.content(this)
            }
        }
    }
}
