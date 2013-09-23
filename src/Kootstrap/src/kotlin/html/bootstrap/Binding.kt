package kotlin.html.bootstrap

import kotlin.html.*

public class BIND(containingTag: HtmlTag) : TransparentTag(containingTag)
public class FETCH(containingTag: HtmlTag) : TransparentTag(containingTag)

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

fun HtmlTag.fetch(dataUrl: Link, interval: Int = 0, content: FETCH.() -> Unit) {
    val t = FETCH(this)
    t.content()
    if (!t.applyAttributes {
        attribute("data-url", dataUrl.href())
        attribute("data-use", "bind")
        if (interval > 0) {
            attribute("data-interval", interval.toString())
        }
    }) throw Exception("No tag to apply attributes to")
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

fun HtmlTag.bind(property: String, content: BIND.() -> Unit) {
    val b = BIND(this)
    b.content()
    if (!b.applyAttributes {
        attribute("bind", property)
    }) throw Exception("No tag to apply attributes to")

}
