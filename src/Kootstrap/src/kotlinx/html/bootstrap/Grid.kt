package kotlinx.html.bootstrap

import kotlinx.html.*

inline fun HtmlBodyTag.container(body: DIV.()->Unit): Unit = div {
    addClass("container")
    body()
}

inline fun HtmlBodyTag.row(body: DIV.()->Unit): Unit = div {
    addClass("row")
    body()
}

inline fun HtmlBodyTag.cell(width: Int, body: DIV.()->Unit): Unit = div {
    addClass("col-sm-$width")
    body()
}

inline fun HtmlBodyTag.cell(width: Int, offset: Int, body: DIV.()->Unit): Unit = div {
    addClass("col-sm-$width")
    addClass("col-sm-offset-$offset")
    body()
}

inline fun HtmlBodyTag.cell(width: devices, body: DIV.()->Unit): Unit = div {
    addClass(width.styles())
    body()
}

inline fun HtmlBodyTag.cell(width: devices, offset: devices, body: DIV.()->Unit): Unit = div {
    addClass(width.styles())
    addClass(offset.styles("offset"))
    body()
}

inline fun HtmlBodyTag.cell(width: devices, offset: Int, body: DIV.()->Unit): Unit = div {
    addClass(width.styles())
    addClass("col-sm-offset-$offset")
    body()
}

inline fun HtmlBodyTag.footer(body: DIV.()->Unit): Unit = div {
    addClass("footer")
    container { body() }
}

inline fun HtmlBodyTag.page(body: DIV.()->Unit): Unit = div {
    addClass("footer-wrap")
    body()

    div {
        addClass("footer-push")
    }
}

fun HtmlBodyTag.pullRight() {
    addClass(pull_right)
}

public class devices(val phone: Int, val tablet: Int = phone, val medium: Int = tablet, val large: Int = medium) {
    public fun styles(name: String = ""): StyleClass {
        val suffix = if (name.length > 0) "-$name" else ""
        return (s("col-xs$suffix-$phone") + s("col-sm$suffix-$tablet") + s("col-md$suffix-$medium") + s("col-lg$suffix-$large"))!!
    }
}
