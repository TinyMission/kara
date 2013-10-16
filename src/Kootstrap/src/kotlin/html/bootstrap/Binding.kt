package kotlin.html.bootstrap

import kotlin.html.*

fun HtmlTag.applyAttributes(apply: HtmlTag.() -> Unit): Boolean {
    for (child in children) {
        if (child is HtmlTag) {
            if (!(child is TransparentTag)) {
                child.apply()
                return true
            }
            if (child.applyAttributes(apply))
                return true
        }
    }
    return false
}

fun <T: HtmlTag> T.fetch(dataUrl: Link, interval: Int = 0, content: T.() -> Unit) {
    val curChildren = children.size
    content()
    if (curChildren + 1 != children.size) {
        throw Exception("Bind must have single child tag")
    }

    with (children.last as HtmlTag) {
        attribute("data-url", dataUrl.href())
        attribute("data-use", "bind")
        if (interval > 0) {
            attribute("data-interval", interval.toString())
        }
    }
}

fun HtmlTag.bindIf(attribute: String, condition: String, trueValue: String, falseValue: String? = null) {
    if (falseValue != null)
        attribute("bind-$attribute", "if:${condition}:${trueValue}:${falseValue}")
    else
        attribute("bind-$attribute", "if:${condition}:${trueValue}")
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

fun <T:HtmlTag> T.bind(property: String, content: T.() -> Unit) {
    val curChildren = children.size
    content()
    if (curChildren + 1 != children.size) {
        throw Exception("Bind must have single child tag")
    }

    (children.last as HtmlTag).attribute("bind", property)
}
