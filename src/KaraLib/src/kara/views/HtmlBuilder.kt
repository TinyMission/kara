package kara

import java.util.*
import kara.internal.*

abstract class HtmlElement(val containingElement: HtmlElement?, val contentStyle: ContentStyle = ContentStyle.block) {
    {
        appendTo(containingElement)
    }

    private fun appendTo(element : HtmlElement?) = element?.children?.add(this)

    val children: MutableList<HtmlElement> = ArrayList<HtmlElement>()

    abstract fun renderElement(appConfig: AppConfig, builder: StringBuilder, indent: String)

    open fun toString(appConfig: AppConfig): String {
        val builder = StringBuilder()
        renderElement(appConfig, builder, "")
        return builder.toString()
    }
}

enum class RenderStyle {
    adaptive
    expanded
    empty
}

enum class ContentStyle {
    block
    text
    propagate
}

private fun HtmlElement.computeContentStyle(): ContentStyle {
    return when (contentStyle) {
        ContentStyle.block, ContentStyle.text -> contentStyle
        ContentStyle.propagate -> if (children.all { it.computeContentStyle() == ContentStyle.text }) ContentStyle.text else ContentStyle.block
    }
}

abstract class HtmlTag(containingTag: HtmlTag?, val tagName: String, val renderStyle: RenderStyle = RenderStyle.expanded, contentStyle: ContentStyle = ContentStyle.block): HtmlElement(containingTag, contentStyle) {
    private val attributes = HashMap<String, String>()

    public fun build<T: HtmlTag>(tag: T, contents: T.() -> Unit): T {
        tag.contents()
        return tag
    }

    override fun renderElement(appConfig: AppConfig, builder: StringBuilder, indent: String) {
        val count = children.size()
        val contentStyle = computeContentStyle()
        builder.append(indent)
        when {
            count == 0 && renderStyle != RenderStyle.expanded -> {
                builder.append("<$tagName${renderAttributes()}/>")
            }
            count != 0 && renderStyle == RenderStyle.empty -> {
                throw InvalidHtmlException("Empty tag has children")
            }
            children.all { it.computeContentStyle() == ContentStyle.text } -> {
                builder.append("<$tagName${renderAttributes()}>")
                for (c in children) {
                    c.renderElement(appConfig, builder, "")
                }
                builder.append("</$tagName>")
            }
            count == 0 -> {
                builder.append("<$tagName${renderAttributes()}></$tagName>")
            }
            else -> {
                builder.append("<$tagName${renderAttributes()}>\n")
                for (c in children) {
                    c.renderElement(appConfig, builder, indent + "  ")
                }
                builder.append("$indent</$tagName>")
            }
        }
        if (indent != "")
            builder.append("\n")
    }

    protected fun renderAttributes(): String? {
        val builder = StringBuilder()
        for (a in attributes.keySet()) {
            val attr = attributes[a]!!
            if (attr.length > 0) {
                builder.append(" $a=\"${attr.htmlEscape()}\"")
            }
        }
        return builder.toString()
    }

    public fun attribute(name: String, value: String) {
        attributes[name] = value
    }

    public fun get(attributeName: String): String {
        val answer = attributes[attributeName]
        if (answer == null) throw RuntimeException("Atrribute $attributeName is missing")
        return answer
    }

    public fun set(attName: String, attValue: String) {
        attributes[attName] = attValue
    }
}

class HtmlText(containingTag: HtmlTag?, private val text: String): HtmlElement(containingTag, ContentStyle.text) {
    override fun renderElement(appConfig: AppConfig, builder: StringBuilder, indent: String) {
        builder.append(indent)
        builder.append(escapedText())
        if (indent != "")
            builder.append("\n")
    }

    public fun escapedText(): String = text.htmlEscape()
}

abstract class HtmlTagWithText(containingTag: HtmlTag?, name: String, renderStyle: RenderStyle = RenderStyle.expanded): HtmlTag(containingTag, name, renderStyle) {
    /**
     * Override the plus operator to add a text element.
     */
    fun String.plus() = HtmlText(this@HtmlTagWithText, this)

    /**
     * Yet another way to set the text content of the node.
     */
    var text: String?
        get() {
            if (children.size > 0)
                return children[0].toString()
            return ""
        }
        set(value) {
            children.clear()
            if (value != null)
                HtmlText(this@HtmlTagWithText, value)
        }
}

abstract class HtmlBodyTagWithText(containingTag: HtmlTag?, name: String, renderStyle: RenderStyle = RenderStyle.expanded, contentStyle: ContentStyle = ContentStyle.block): HtmlBodyTag(containingTag, name, renderStyle, contentStyle) {
    /**
     * Override the plus operator to add a text element.
     */
    fun String.plus() = HtmlText(this@HtmlBodyTagWithText, this)

    /**
     * Yet another way to set the text content of the node.
     */
    var text: String?
        get() {
            if (children.size > 0)
                return children[0].toString()
            return ""
        }
        set(value) {
            children.clear()
            if (value != null)
                HtmlText(this@HtmlBodyTagWithText, value)
        }
}

class InvalidHtmlException(val message: String): RuntimeException(message) {

}
