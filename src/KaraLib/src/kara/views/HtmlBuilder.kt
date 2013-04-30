package kara

import java.util.*
import kara.internal.*

abstract class HtmlElement(val containingTag: HtmlTag?) {
    {
        val parent = containingTag
        if (parent != null)
            parent.children.add(this)
    }

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

abstract class HtmlTag(containingTag: HtmlTag?, val tagName: String, val renderStyle: RenderStyle = RenderStyle.expanded): HtmlElement(containingTag) {
    val children: MutableList<HtmlElement> = ArrayList<HtmlElement>()
    private val attributes = HashMap<String, String>()

    public fun build<T: HtmlTag>(tag: T, contents: T.() -> Unit): T {
        tag.contents()
        return tag
    }

    override fun renderElement(appConfig: AppConfig, builder: StringBuilder, indent: String) {
        val count = children.count()
        when {
            count == 0 && renderStyle != RenderStyle.expanded -> {
                builder.append("$indent<$tagName${renderAttributes()}/>\n")
            }
            count != 0 && renderStyle == RenderStyle.empty -> {
                throw InvalidHtmlException("Empty tag has children")
            }
            count == 1 && children[0] is HtmlText -> {
                // for single text elements, render inline
                builder.append("$indent<$tagName${renderAttributes()}>")
                builder.append((children[0] as HtmlText).escapedText())
                builder.append("</$tagName>\n")
            }
            count == 0 -> {
                builder.append("$indent<$tagName${renderAttributes()}>")
                builder.append("</$tagName>\n")
            }
            else -> {
                builder.append("$indent<$tagName${renderAttributes()}>\n")
                for (c in children) {
                    c.renderElement(appConfig, builder, indent + "  ")
                }
                builder.append("$indent</$tagName>\n")
            }
        }
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

    public fun get<T>(attr: Attribute<T>): T {
        val answer = attributes[attr.name]
        if (answer == null) throw RuntimeException("Atrribute ${attr.name} is missing")
        return attr.decode(answer)
    }

    public fun set<T>(attr: Attribute<T>, value: T) {
        attributes[attr.name] = attr.encode(value)
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

class HtmlText(containingTag: HtmlTag?, private val text: String): HtmlElement(containingTag) {
    override fun renderElement(appConfig: AppConfig, builder: StringBuilder, indent: String) {
        builder.append(indent)
        builder.append(escapedText())
        builder.append('\n')
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

abstract class HtmlBodyTagWithText(containingTag: HtmlTag?, name: String, renderStyle: RenderStyle = RenderStyle.expanded): HtmlBodyTag(containingTag, name, renderStyle) {
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
