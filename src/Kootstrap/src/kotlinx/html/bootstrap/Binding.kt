package kotlinx.html.bootstrap

import java.util.*
import kotlinx.html.*

fun HtmlTag.applyAttributes(apply: HtmlTag.() -> Unit): Boolean {
    for (child in children) {
        if (child is HtmlTag) {
            if (child !is TransparentTag) {
                child.apply()
                return true
            }
            if (child.applyAttributes(apply))
                return true
        }
    }
    return false
}

fun <T : HtmlTag> T.withAttributes(content: T.() -> Unit, apply: HtmlTag.() -> Unit) {
    val curChildren = children.size
    content()
    if (curChildren + 1 != children.size) {
        throw Exception("Template tag must have single child tag")
    }

    with(children.lastOrNull() as HtmlTag, apply)
}

fun <T : HtmlTag> T.bind(property: String, content: T.() -> Unit) {
    withAttributes(content) {
        attribute("bind", property)
    }
}

fun <T : HtmlTag> T.fetch(dataUrl: Link, interval: Int = 0, content: T.() -> Unit) {
    withAttributes(content) {
        attribute("data-url", dataUrl.href())
        attribute("data-use", "bind")
        if (interval > 0) {
            attribute("data-interval", interval.toString())
        }
    }
}

fun HtmlTag.bindIf(attribute: String, condition: String, trueValue: String, falseValue: String? = null) {
    if (falseValue != null)
        attribute("bind-$attribute", "if:$condition:$trueValue:$falseValue")
    else
        attribute("bind-$attribute", "if:$condition:$trueValue")
}

fun HtmlTag.bind(attribute: String, property: String) {
    attribute("bind-$attribute", property)
}

fun HtmlTag.bindText(property: String) {
    attribute("bind-text", property)
}

fun HtmlTag.bindHtml(property: String) {
    attribute("bind-html", property)
}

class SendContext(val tag: HtmlBodyTag) {
    fun bindResponse(selector: String) {
        tag.attribute("data-use", "bind")
        tag.attribute("data-bind", selector)
    }

    fun refresh(selector: String) {
        tag.attribute("send-fetch", selector)
    }
}

class LinkWithParameters(val link: Link) {
    val parameters = HashMap<String, String>()
    fun put(name: String, value: String): LinkWithParameters {
        parameters.put(name, value)
        return this
    }
}

fun Link.param(name: String): LinkWithParameters = LinkWithParameters(this).put(name, name)
fun LinkWithParameters.param(name: String): LinkWithParameters = this.put(name, name)
fun Link.param(name: String, value: String): LinkWithParameters = LinkWithParameters(this).put(name, value)
fun LinkWithParameters.param(name: String, value: String): LinkWithParameters = this.put(name, value)

fun HtmlBodyTag.send(url: Link, httpMethod: FormMethod = FormMethod.post): SendContext {
    attribute("send-url", url.href())
    attribute("send-method", httpMethod.toString().toUpperCase())
    return SendContext(this)
}

fun HtmlBodyTag.send(url: LinkWithParameters): SendContext {
    attribute("send-values", url.parameters.asSequence().joinToString(","))
    return send(url.link)
}

