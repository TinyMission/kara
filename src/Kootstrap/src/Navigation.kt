package kotlin.html.bootstrap

import kotlin.html.*

public fun ListTag.item(active: Boolean = false, c: StyleClass? = null, body: LI.()->Unit): Unit = li(c + if (active) s("active") else null, contents = body)
public fun ListTag.item(url: Link, active: Boolean = false, c: StyleClass? = null, body: A.()->Unit): Unit = item(active,c) { a { href = url; body() } }
public fun ListTag.item(name: String, active: Boolean = false, c: StyleClass? = null, body: A.()->Unit): Unit = item(active,c) { a { href = DirectLink("#$name"); body() } }
public fun ListTag.itemDivider(): Unit = li(s("divider"))

public fun ListTag.dropdownItem(c: StyleClass? = null, caption: A.()->Unit, items: UL.()->Unit): Unit = li(s("dropdown") + c) {
    a {
        this["href"] = "#"
        this["data-toggle"] = "dropdown"
        caption()
    }
    menu {
        items()
    }
}


public fun HtmlBodyTag.listGroup(body: UL.()->Unit): Unit = ul(s("list-group"), contents = body)
public fun ListTag.listItem(active: Boolean = false, body: LI.()->Unit): Unit = item(active, s("list-group-item"), body)
public fun ListTag.listItem(url: Link, active: Boolean = false, body: A.()->Unit): Unit = item(url, active, s("list-group-item"), body)

public fun HtmlBodyTag.linkGroup(body: DIV.()->Unit): Unit = div(s("list-group"), contents = body)
public fun DIV.linkItem(active: Boolean = false, body: A.()->Unit): Unit = a(s("list-group-item") + if (active) s("active") else null, contents = body)
public fun DIV.linkItem(url: Link, active: Boolean = false, body: A.()->Unit): Unit = linkItem(active) { href = url; body() }

public fun HtmlBodyTag.menu(body: UL.()->Unit): Unit = ul(s("dropdown-menu"), contents = body)

public enum class navbarPosition {
    default top bottom static
}

public fun HtmlBodyTag.navbar(position: navbarPosition, body: DIV.()->Unit): Unit = div(s("navbar navbar-default") + when(position) {
    navbarPosition.top -> s("navbar-fixed-top")
    navbarPosition.bottom -> s("navbar-fixed-bottom")
    navbarPosition.static -> s("navbar-static-top")
    else -> null
}, contents = body)

public fun HtmlBodyTag.navbarHeader(body: DIV.()->Unit): Unit = div(s("navbar-header"), contents = body)
public fun HtmlBodyTag.navbarBrand(c: StyleClass? = null, body: A.()->Unit): Unit = a(s("navbar-brand") + c, contents = body)
public fun HtmlBodyTag.navbarCollapse(body: DIV.()->Unit): Unit = div(s("collapse navbar-collapse navbar-menu-collapse"), contents = body)
public fun HtmlBodyTag.navbarCollapseToggle(body: BUTTON.()->Unit): Unit = button(s("navbar-toggle")) {
    buttonType = ButtonType.button
    attribute("data-toggle", "collapse")
    attribute("data-target", ".navbar-menu-collapse")
    span(s("sr-only")) { +"Toggle navigation" }
    body()
}
public fun HtmlBodyTag.navbarGroup(body: UL.()->Unit): Unit = ul(s("nav navbar-nav"), contents = body)
public fun HtmlBodyTag.navbarGroupRight(body: UL.()->Unit): Unit = ul(s("nav navbar-nav navbar-right pull-right"), contents = body)
public fun HtmlBodyTag.navbarGroupLeft(body: UL.()->Unit): Unit = ul(s("nav navbar-nav navbar-left"), contents = body)

public fun HtmlBodyTag.breadcrumb(c: StyleClass? = null, body: UL.()->Unit): Unit = ul(s("breadcrumb") + c, contents = body)
