package kotlinx.html

fun HTML.head(init: HEAD.() -> Unit) = build(HEAD(this), init)
fun HEAD.title(init: TITLE.() -> Unit = { }) = build(TITLE(this), init)

fun HEAD.title(text: String) {
    build(TITLE(this), { +text })
}

fun HEAD.link(href: Link, rel: String = "stylesheet", mimeType: String = "text/css", content: _LINK.() -> Unit = { }) {
    val tag = build(_LINK(this), content)
    tag.href = href
    tag.rel = rel
    tag.mimeType = mimeType
}

fun HEAD.meta(name: String, content: String) {
    val tag = build(META(this), { })
    tag.name = name
    tag.content = content
}

fun HEAD.base(href: String, target: String) {
    val tag = build(BASE(this), { })
    tag.href = href
    tag.target = target
}

fun HtmlTag.script(src: Link, mimeType: String = "text/javascript") {
    val tag = build(SCRIPTSRC(this), { })
    tag.src = src
    tag.mimeType = mimeType
}

fun HtmlTag.script(mimeType: String = "text/javascript", content: SCRIPTBLOCK.() -> Unit) {
    val tag = build(SCRIPTBLOCK(this), content)
    tag.mimeType = mimeType
}

class HEAD(containingTag: HTML) : HtmlTag(containingTag, "head") {
}

class META(containingTag: HEAD) : HtmlTag(containingTag, "meta") {
    public var name: String by Attributes.name
    public var content: String by StringAttribute("content")
}

class BASE(containingTag: HEAD) : HtmlTag(containingTag, "base") {
    public var href: String by StringAttribute("href")
    public var target: String by StringAttribute("target")
}

class _LINK(containingTag: HEAD) : HtmlTag(containingTag, "link", RenderStyle._empty) {
    public var href: Link by Attributes.href
    public var rel: String by Attributes.rel
    public var mimeType: String by Attributes.mimeType
    init {
        rel = "stylesheet"
        mimeType = "text/css"
    }
}

class SCRIPTSRC(containingTag: HtmlTag) : HtmlTag(containingTag, "script") {
    public var src: Link by Attributes.src
    public var mimeType: String by Attributes.mimeType
    init {
        mimeType = "text/javascript"
    }
}

class SCRIPTBLOCK(containingTag: HtmlTag) : HtmlTag(containingTag, "script") {
    public var mimeType: String by Attributes.mimeType
    init {
        mimeType = "text/javascript"
    }
}

class TITLE(containingTag: HEAD) : HtmlTag(containingTag, "title") {
}
