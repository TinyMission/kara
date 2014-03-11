package kotlin.html.bootstrap

import kotlin.html.*

val form_horizontal = s("form-horizontal")
val form_control = s("form-control")

public fun HtmlBodyTag.blockAction(h: highlight = highlight.default, body: A.()->Unit): Unit = a {
    addClass("btn btn-block btn-${h.name()}")
    body()
}

public fun HtmlBodyTag.action(h: highlight = highlight.default,
                              size: caliber = caliber.default,
                              body: A.()->Unit): Unit = a {
    addClass("btn")
    if (size.value.length() > 0) {
        addClass("btn-${size.value}")
    }
    if (h != highlight.default) {
        addClass("btn-${h.name()}")
    }
    body()
}

public fun HtmlBodyTag.action(url: Link, h: highlight = highlight.default, size: caliber = caliber.default, body: A.()->Unit): Unit = action(h, size) { href = url; body() }

public fun HtmlBodyTag.bt_button(h: highlight = highlight.default, size: caliber = caliber.default, body: BUTTON.()->Unit): Unit = button {
    addClass("btn")

    if (size.value.length() > 0) addClass("btn-${size.value}")
    addClass("btn-${h.name()}")

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
    addClass("btn btn-block btn-${h.name()}")
    body()
}

inline fun HtmlBodyTag.controlWithIcon(iconName: String, body: DIV.()->Unit): Unit = div(s("input-group")) { span(s("input-group-addon")) { icon(iconName) }; body() }
inline fun HtmlBodyTag.controlGroup(body: DIV.()->Unit): Unit = div {
    addClass("form-group")
    body()
}

inline fun HtmlBodyTag.controlLabel(body: LABEL.()->Unit): Unit = label {
    addClass("control-label")
    body()
}

fun HtmlBodyTag.icon(name : String) : Unit = icon(name, null)

fun HtmlBodyTag.icon(name : String, style: StyleClass?) : Unit = i {
    addClass("icon icon-$name")
    addClass(style)
}
