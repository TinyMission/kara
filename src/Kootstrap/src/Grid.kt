package kotlin.html.bootstrap

import kotlin.html.*

public fun HtmlBodyTag.container(c: StyleClass? = null, body: DIV.()->Unit): Unit = div(s("container") + c, contents = body)
public fun HtmlBodyTag.row(c: StyleClass? = null, body: DIV.()->Unit): Unit = div(s("row") + c, contents = body)
public fun HtmlBodyTag.cell(width: Int, c: StyleClass? = null, body: DIV.()->Unit): Unit = div(s("col-sm-$width") + c, contents = body)
public fun HtmlBodyTag.cell(width: Int, offset: Int, c: StyleClass? = null, body: DIV.()->Unit): Unit = div(s("col-sm-$width") + s("col-sm-offset-$offset") + c, contents = body)
public fun HtmlBodyTag.cell(width: devices, c: StyleClass? = null, body: DIV.()->Unit): Unit = div(width.styles() + c, contents = body)
public fun HtmlBodyTag.cell(width: devices, offset: devices, c: StyleClass? = null, body: DIV.()->Unit): Unit = div(width.styles() + offset.styles("offset") + c, contents = body)
public fun HtmlBodyTag.cell(width: devices, offset: Int, c: StyleClass? = null, body: DIV.()->Unit): Unit = div(width.styles() + s("col-sm-offset-$offset") + c, contents = body)

public class devices(val phone: Int, val tablet: Int = phone, val medium: Int = tablet, val large: Int = medium) {
    public fun styles(name: String = ""): StyleClass? {
        val suffix = if (name.length() > 0) "-$name" else ""
        return s("col-xs$suffix-$phone") + s("col-sm$suffix-$tablet") + s("col-md$suffix-$medium") + s("col-lg$suffix-$large")
    }
}