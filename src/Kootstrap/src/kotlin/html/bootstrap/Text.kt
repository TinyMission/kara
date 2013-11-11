package kotlin.html.bootstrap

import kotlin.html.*

public fun HtmlBodyTag.label(h : highlight, body: SPAN.()->Unit) : Unit = span(s("label label-${h.name()}"), contents = body)
public fun HtmlBodyTag.label(url: Link, h : highlight, body: A.()->Unit) : Unit = a(s("label label-${h.name()}")) { href = url; body() }

public fun HtmlBodyTag.blockLabel(h : highlight, body: P.()->Unit) : Unit = p(s("label label-${h.name()}")) { style="display:block;margin-top: 10px;"; body() }

public fun HtmlBodyTag.alert(h : highlight, body: DIV.()->Unit) : Unit = div(s("alert alert-${h.name()}"), contents = body)
public fun HtmlBodyTag.badge(h : highlight, body: DIV.()->Unit) : Unit = div(s("badge badge-${h.name()}"), contents = body)

public fun HtmlBodyTag.text(h : highlight = highlight.default, c : StyleClass? = null, body: SPAN.()->Unit) : Unit = span(s("text-${h.name()}") + c, contents = body)
public fun HtmlBodyTag.lead(c : StyleClass? = null, body: SPAN.()->Unit) : Unit = span(s("lead") + c, contents = body)
public fun HtmlBodyTag.well(c : StyleClass? = null, body: DIV.()->Unit) : Unit = div(s("well") + c, contents = body)

public fun HtmlBodyTag.panel(h : highlight = highlight.default, body: DIV.()->Unit) : Unit = div(s("panel panel-${h.name()}"), contents = body)
public fun HtmlBodyTag.panelHeading(body: DIV.()->Unit) : Unit = div(s("panel-heading"), contents = body)
public fun HtmlBodyTag.panelTitle(body: H3.()->Unit) : Unit = h3(s("panel-title"), contents = body)
public fun HtmlBodyTag.panelBody(body: DIV.()->Unit) : Unit = div(s("panel-body"), contents = body)

public fun HtmlBodyTag.jumbo(body: DIV.()->Unit) : Unit = div(s("jumbotron"), contents = body)

public fun HtmlBodyTag.muted(c : StyleClass? = null, body: SPAN.()->Unit) : Unit = span(s("text-muted")+c, contents = body)
public fun HtmlBodyTag.mutedSmall(c : StyleClass? = null, body: SMALL.()->Unit) : Unit = small(s("text-muted")+c, contents = body)


