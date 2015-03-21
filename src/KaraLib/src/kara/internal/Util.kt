package kara.internal

import java.util.*
import kotlin.html.*

/*
class HtmlBodyTagBuilder(val outer: HtmlBodyTag.(HtmlBodyTag.() -> Unit) -> Unit) {
    fun HtmlBodyTag.invoke(inner: HtmlBodyTag.() -> Unit) { outer(inner) }
}

public fun HtmlBodyTag.testFetch() {
    val tag = fetch("#".link()).every(5)
    tag {
        span { +"text" }
    }
}

fun HtmlBodyTagBuilder.every(duration : Int) : HtmlBodyTagBuilder = HtmlBodyTagBuilder {
    outer {
        attribute("every", "$duration")
        it()
    }
}

fun HtmlBodyTag.fetch(url: Link) = HtmlBodyTagBuilder {
    div {
        it()
    }
}

*/

