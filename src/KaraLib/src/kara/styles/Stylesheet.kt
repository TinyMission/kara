package kara

import kotlin.html.*
import java.io.OutputStreamWriter
import java.io.FileOutputStream

/** A class for programmatically generating CSS stylesheets.
 */
abstract class Stylesheet(var namespace : String = "") : StaticResource() {
    /** Subclasses should override this to actual perform the stylesheet building.
    */
    abstract fun CssElement.render()

    var cache: Pair<ByteArray, Long>? = null

    fun toString() : String {
        val element = CssElement()
        element.render()
        val builder = StringBuilder()
        for (child in element.children) {
            child.build(builder, "")
        }
        return builder.toString()
    }

    override fun content(): ResourceContent {
        val (bytes, stamp) = cache ?: run {
            cache = Pair(toString().toByteArray("UTF-8"), System.currentTimeMillis())
            cache!!
        }
        return ResourceContent("text/css", stamp, bytes.size, {bytes.inputStream})
    }
}

fun HEAD.style(media: String = "all", mimeType: String = "text/css", buildSheet: CssElement.() -> Unit) {
    val stylesheet = object : Stylesheet("") {
        override fun CssElement.render() {
            buildSheet()
        }
    }
    val tag = build(STYLE(this, stylesheet), { })
    tag.media = media
    tag.mimeType = mimeType
}

fun HEAD.stylesheet(stylesheet: Stylesheet)  = build(STYLESHEETLINK(this, stylesheet), { })

class STYLE(containingTag : HEAD, val stylesheet : Stylesheet) : HtmlTag(containingTag, "style") {
    public var media : String by StringAttribute("media")
    public var mimeType : String by Attributes.mimeType

    {
        media = "all"
        mimeType = "text/css"
    }

    override fun renderElement(builder: StringBuilder, indent: String) {
        builder.append("$indent<$tagName${renderAttributes()}>\n")
        builder.append(stylesheet.toString())
        builder.append("$indent</$tagName>\n")
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


    override fun renderElement(builder: StringBuilder, indent: String) {
        href = stylesheet
        super<HtmlTag>.renderElement(builder, indent)
    }
}
