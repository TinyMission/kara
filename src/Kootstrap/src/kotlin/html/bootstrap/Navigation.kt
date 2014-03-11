package kotlin.html.bootstrap

import kotlin.html.*

public fun ListTag.item(active: Boolean = false, body: LI.()->Unit): Unit = li(if (active) s("active") else null, contents = body)
public fun ListTag.item(url: Link, active: Boolean = false, body: A.()->Unit): Unit = item(active) { a { href = url; body() } }
public fun ListTag.item(name: String, active: Boolean = false, body: A.()->Unit): Unit = item(active) { a { href = DirectLink("#$name"); body() } }
public fun ListTag.itemDivider(): Unit = li(s("divider")) {}

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


inline fun HtmlBodyTag.listGroup(body: UL.()->Unit): Unit = ul(s("list-group"), contents = body)
public fun ListTag.listItem(active: Boolean = false, body: LI.()->Unit): Unit = item(active) {
    addClass("list-group-item")
    body()
}

public fun ListTag.listItem(url: Link, active: Boolean = false, body: A.()->Unit): Unit = item(url, active) {
    addClass("list-group-item")
    body()
}
fun ListTag.listDivider(): Unit = span(s("list-group-divider")) {}

inline fun HtmlBodyTag.linkGroup(body: DIV.()->Unit): Unit = div(s("list-group"), contents = body)
public fun DIV.linkItem(active: Boolean = false, body: A.()->Unit): Unit = a(s("list-group-item") + if (active) s("active") else null, contents = body)
public fun DIV.linkItem(url: Link, active: Boolean = false, body: A.()->Unit): Unit = linkItem(active) { href = url; body() }
public fun DIV.linkDivider(): Unit = span(s("list-group-divider")) {}

inline  fun HtmlBodyTag.menu(body: UL.()->Unit): Unit = ul(s("dropdown-menu"), contents = body)

public enum class navbarPosition {
    default top bottom static
}

public fun HtmlBodyTag.navbar(position: navbarPosition, body: DIV.()->Unit): Unit = div(s("navbar navbar-default") + when(position) {
    navbarPosition.top -> s("navbar-fixed-top")
    navbarPosition.bottom -> s("navbar-fixed-bottom")
    navbarPosition.static -> s("navbar-static-top")
    else -> null
}, contents = body)

inline fun HtmlBodyTag.navbarHeader(body: DIV.()->Unit): Unit = div(s("navbar-header"), contents = body)
inline fun HtmlBodyTag.navbarBrand(body: A.()->Unit): Unit = a(s("navbar-brand"), contents = body)
inline fun HtmlBodyTag.navbarCollapse(body: DIV.()->Unit): Unit = div(s("collapse navbar-collapse navbar-menu-collapse"), contents = body)
inline fun HtmlBodyTag.navbarCollapseToggle(body: BUTTON.()->Unit): Unit = button {
    addClass("navbar-toggle")
    buttonType = ButtonType.button
    attribute("data-toggle", "collapse")
    attribute("data-target", ".navbar-menu-collapse")
    span(s("sr-only")) { +"Toggle navigation" }
    body()
}
inline fun HtmlBodyTag.navbarGroup(body: UL.()->Unit): Unit = ul(s("nav navbar-nav"), contents = body)
inline fun HtmlBodyTag.navbarGroupRight(body: UL.()->Unit): Unit = ul(s("nav navbar-nav navbar-right pull-right"), contents = body)
inline fun HtmlBodyTag.navbarGroupLeft(body: UL.()->Unit): Unit = ul(s("nav navbar-nav navbar-left"), contents = body)

inline fun HtmlBodyTag.breadcrumb(body: UL.()->Unit): Unit = ul(s("breadcrumb"), contents = body)
