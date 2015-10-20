package kotlin.html.bootstrap

import kotlin.html.*

inline fun HtmlBodyTag.label(h : highlight, body: SPAN.()->Unit) : Unit = span("label label-${h.name}", contents = body)
inline fun HtmlBodyTag.label(url: Link, h : highlight, body: A.()->Unit) : Unit = a("label label-${h.name}") { href = url; body() }

inline fun HtmlBodyTag.blockLabel(h : highlight, body: P.()->Unit) : Unit = p("label label-${h.name}") { style="display:block;margin-top: 10px;"; body() }

inline fun HtmlBodyTag.alert(h : highlight, body: DIV.()->Unit) : Unit = div("alert alert-${h.name}", contents = body)
inline fun HtmlBodyTag.badge(h : highlight, body: DIV.()->Unit) : Unit = div("badge badge-${h.name}", contents = body)

fun HtmlBodyTag.text(h : highlight = highlight.default, body: SPAN.()->Unit) : Unit = span("text-${h.name}", contents = body)
inline fun HtmlBodyTag.lead(body: SPAN.()->Unit) : Unit = span("lead", contents = body)
inline fun HtmlBodyTag.well(body: DIV.()->Unit) : Unit = div("well", contents = body)

fun HtmlBodyTag.panel(h : highlight = highlight.default, body: DIV.()->Unit) : Unit = div("panel panel-${h.name}", contents = body)
inline fun HtmlBodyTag.panelHeading(body: DIV.()->Unit) : Unit = div("panel-heading", contents = body)
inline fun HtmlBodyTag.panelTitle(body: H3.()->Unit) : Unit = h3 {
    addClass("panel-title")
    body()
}
inline fun HtmlBodyTag.panelBody(body: DIV.()->Unit) : Unit = div("panel-body", contents = body)

inline fun HtmlBodyTag.jumbo(body: DIV.()->Unit) : Unit = div("jumbotron", contents = body)

inline fun HtmlBodyTag.muted(body: SPAN.()->Unit) : Unit = span("text-muted", contents = body)
inline fun HtmlBodyTag.mutedSmall(body: SMALL.()->Unit) : Unit = small {
    addClass("text-muted")
    body()
}


