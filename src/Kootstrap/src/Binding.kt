package kotlin.html.bootstrap

import kotlin.html.*

public class BIND(containingTag : HtmlTag): TransparentTag(containingTag)

public class TEMPLATE(containingTag: HtmlTag): TransparentTag(containingTag)

fun HtmlBodyTag.template(dataUrl: Link, content: TEMPLATE.() -> Unit) {
    val t = TEMPLATE(this)
    t.content()

    for (child in t.children) {
        if (child is HtmlBodyTag) {
            child["data-url"] = dataUrl.href()
            val oldClasses = child.tryGet("class")
            if (oldClasses == null) {
                child.c = s("tempalte")
            }
            else {
                child.c = s("template $oldClasses")
            }
        }
    }
}

fun HtmlBodyTag.bind(attribute: String, property: String) {
    attribute("bind-$attribute", property)
}

fun HtmlBodyTag.bindText(property: String) {
    attribute("bind-text", property)
}

fun HtmlBodyTag.bind(property: String, content: BIND.() -> Unit) {
    val b = BIND(this)
    b.content()

    for (child in b.children) {
        if (child is HtmlTag) {
            child["data-bind"] = property
        }
    }
}
