package kara

import kara.internal.*

fun HTML.head(init: HEAD.() -> Unit) = build(HEAD(this), init)
fun HEAD.title(init: TITLE.() -> Unit = { }) = build(TITLE(this), init)

fun HEAD.title(text: String) {
    build(TITLE(this), { +text })
}

fun HEAD.link(href: Link, rel: String = "stylesheet", mimeType: String = "text/javascript") {
    val tag = build(_LINK(this), { })
    tag.href = href
    tag.rel = rel
    tag.mimeType = mimeType
}

fun HEAD.meta(name: String, content: String) {
    val tag = build(META(this), { })
    tag.name = name
    tag.content = content
}

fun HtmlTag.script(src: Link, mimeType: String = "text/javascript") {
    val tag = build(SCRIPTSRC(this), { })
    tag.src = src
    tag.mimeType = mimeType
}

fun HtmlTag.script(mimeType: String = "text/javascript", content : SCRIPTBLOCK.() -> Unit) {
    val tag = build(SCRIPTBLOCK(this), content)
    tag.mimeType = mimeType
}

fun HEAD.style(media: String = "all", mimeType: String = "text/css", init: Stylesheet.() -> Unit) {
    val stylesheet = object : Stylesheet() {
        override fun render() {
            this.init()
        }
    }
    val tag = build(STYLE(this, stylesheet), { })
    tag.media = media
    tag.mimeType = mimeType
}

fun HEAD.stylesheet(stylesheet: Stylesheet)  = build(STYLESHEETLINK(this, stylesheet), { })

class HEAD(containingTag: HTML): HtmlTagWithText(containingTag, "head") {}

class STYLE(containingTag : HEAD, val stylesheet : Stylesheet) : HtmlTagWithText(containingTag, "style") {
    public var media : String by StringAttribute("media")
    public var mimeType : String by Attributes.mimeType

    {
        media = "all"
        mimeType = "text/css"
    }

    override fun renderElement(appConfig : AppConfig, builder: StringBuilder, indent: String) {
        builder.append("$indent<$tagName${renderAttributes()}>\n")
        builder.append(stylesheet.toString())
        builder.append("$indent</$tagName>\n")
    }
}


class META(containingTag : HEAD) : HtmlTag(containingTag, "meta") {
    public var name : String by Attributes.name
    public var content : String by StringAttribute("content")
}

class _LINK(containingTag : HEAD) : HtmlTag(containingTag, "link", RenderStyle.empty) {
    public var href : Link by Attributes.href
    public var rel : String by Attributes.rel
    public var mimeType : String by Attributes.mimeType
    {
        rel = "stylesheet"
        mimeType = "text/css"
    }
}

class SCRIPTSRC(containingTag : HtmlTag) : HtmlTag(containingTag, "script") {
    public var src : Link by Attributes.src
    public var mimeType : String by Attributes.mimeType
    {
        mimeType = "text/javascript"
    }
}

class SCRIPTBLOCK(containingTag : HtmlTag) : HtmlTagWithText(containingTag, "script") {
    public var mimeType : String by Attributes.mimeType
    {
        mimeType = "text/javascript"
    }
}

class STYLESHEETLINK(containingTag : HEAD, var stylesheet : Stylesheet) : HtmlTag(containingTag, "link", RenderStyle.empty) {
    public var href : Link by Attributes.href
    public var rel : String by Attributes.rel
    public var mimeType : String by Attributes.mimeType
    {
        rel = "stylesheet"
        mimeType = "text/css"
    }


    override fun renderElement(appConfig : AppConfig, builder: StringBuilder, indent: String) {
        stylesheet.write(appConfig)
        href = stylesheet.relativePath(appConfig).link()
        super<HtmlTag>.renderElement(appConfig, builder, indent)
    }
}

class TITLE(containingTag : HEAD) : HtmlTagWithText(containingTag, "title") {}

