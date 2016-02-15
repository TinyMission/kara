package kotlinx.html.bootstrap

import kotlinx.html.*

val form_horizontal = "form-horizontal"
val form_control = "form-control"

public fun HtmlBodyTag.blockAction(h: highlight = highlight.default, body: A.()->Unit): Unit = a {
    addClass("btn btn-block btn-${h.name}")
    body()
}

public fun HtmlBodyTag.action(h: highlight = highlight.default,
                              size: caliber = caliber.default,
                              body: A.()->Unit): Unit = a {
    addClass("btn")
    if (size.value.length > 0) {
        addClass("btn-${size.value}")
    }
    if (h != highlight.default) {
        addClass("btn-${h.name}")
    }
    body()
}

public fun HtmlBodyTag.action(url: Link, h: highlight = highlight.default, size: caliber = caliber.default, body: A.()->Unit): Unit = action(h, size) { href = url; body() }

public fun HtmlBodyTag.bt_button(h: highlight = highlight.default, size: caliber = caliber.default, body: BUTTON.()->Unit): Unit = button {
    addClass("btn")

    if (size.value.length > 0) addClass("btn-${size.value}")
    addClass("btn-${h.name}")

    body()
}

public fun HtmlBodyTag.bt_button(url: Link, h: highlight = highlight.default, size: caliber = caliber.default, body: BUTTON.()->Unit): Unit = bt_button(h, size) {
    href = url;
    body()
}

inline fun HtmlBodyTag.actionGroup(body: HtmlBodyTag.()->Unit): Unit = p {
    body()
}

inline fun HtmlBodyTag.blockButton(h: highlight, body: BUTTON.()->Unit): Unit = button {
    addClass("btn btn-block btn-${h.name}")
    body()
}

inline fun HtmlBodyTag.controlWithIcon(iconName: String, body: DIV.()->Unit): Unit = div("input-group") { span("input-group-addon") { icon(iconName) }; body() }
inline fun HtmlBodyTag.controlGroup(body: DIV.()->Unit): Unit = div("form-group", body)

inline fun HtmlBodyTag.controlLabel(body: LABEL.()->Unit): Unit = label("control-label", body)

fun HtmlBodyTag.icon(name : String, c: String?=null) : Unit = i {
    addClass("icon icon-$name")
    if (c != null) {
        addClass(c)
    }
}
