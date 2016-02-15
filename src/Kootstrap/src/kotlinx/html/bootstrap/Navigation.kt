package kotlinx.html.bootstrap

import kotlinx.html.*

public fun ListTag.item(active: Boolean = false, body: LI.()->Unit): Unit = li(if (active) "active" else "", contents = body)
public fun ListTag.item(url: Link, active: Boolean = false, body: A.()->Unit): Unit = item(active) { a { href = url; body() } }
public fun ListTag.item(name: String, active: Boolean = false, body: A.()->Unit): Unit = item(active) { a { href = DirectLink("#$name"); body() } }
public fun ListTag.itemDivider(): Unit = li("divider") {}

public fun ListTag.dropdownItem(c:StyleClass? = null, caption: A.()->Unit, items: UL.()->Unit): Unit = li {
    addClass("dropdown")
    addClass(c)
    a {
        this["href"] = "#"
        this["data-toggle"] = "dropdown"
        caption()
    }
    menu {
        items()
    }
}


inline fun HtmlBodyTag.listGroup(body: UL.()->Unit): Unit = ul("list-group", contents = body)
public fun ListTag.listItem(active: Boolean = false, body: LI.()->Unit): Unit = item(active) {
    addClass("list-group-item")
    body()
}

public fun ListTag.listItem(url: Link, active: Boolean = false, body: A.()->Unit): Unit = item(url, active) {
    addClass("list-group-item")
    body()
}
fun ListTag.listDivider(): Unit = span("list-group-divider") {}

inline fun HtmlBodyTag.linkGroup(body: DIV.()->Unit): Unit = div("list-group", contents = body)
public fun DIV.linkItem(active: Boolean = false, body: A.()->Unit): Unit = a(if (active) "list-group-item active" else "list-group-item", contents = body)
public fun DIV.linkItem(url: Link, active: Boolean = false, body: A.()->Unit): Unit = linkItem(active) { href = url; body() }
public fun DIV.linkDivider(): Unit = span("list-group-divider") {}

inline  fun HtmlBodyTag.menu(body: UL.()->Unit): Unit = ul("dropdown-menu", contents = body)

public enum class navbarPosition {
    `default`, top, bottom, static
}

public fun HtmlBodyTag.navbar(position: navbarPosition, body: DIV.()->Unit): Unit = div("navbar navbar-default" + when(position) {
    navbarPosition.top -> " navbar-fixed-top"
    navbarPosition.bottom -> " navbar-fixed-bottom"
    navbarPosition.static -> " navbar-static-top"
    else -> ""
}, contents = body)

inline fun HtmlBodyTag.navbarHeader(body: DIV.()->Unit): Unit = div("navbar-header", contents = body)
inline fun HtmlBodyTag.navbarBrand(body: A.()->Unit): Unit = a("navbar-brand", contents = body)
inline fun HtmlBodyTag.navbarCollapse(body: DIV.()->Unit): Unit = div("collapse navbar-collapse navbar-menu-collapse", contents = body)
inline fun HtmlBodyTag.navbarCollapseToggle(body: BUTTON.()->Unit): Unit = button {
    addClass("navbar-toggle")
    buttonType = ButtonType.button
    attribute("data-toggle", "collapse")
    attribute("data-target", ".navbar-menu-collapse")
    span("sr-only") { +"Toggle navigation" }
    body()
}
inline fun HtmlBodyTag.navbarGroup(body: UL.()->Unit): Unit = ul("nav navbar-nav", contents = body)
inline fun HtmlBodyTag.navbarGroupRight(body: UL.()->Unit): Unit = ul("nav navbar-nav navbar-right pull-right", contents = body)
inline fun HtmlBodyTag.navbarGroupLeft(body: UL.()->Unit): Unit = ul("nav navbar-nav navbar-left", contents = body)

inline fun HtmlBodyTag.breadcrumb(body: UL.()->Unit): Unit = ul("breadcrumb", contents = body)
