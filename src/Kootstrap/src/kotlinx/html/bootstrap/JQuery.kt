package kotlinx.html.bootstrap

import kara.JsonObject
import kara.Request
import kara.jsonValue
import kara.link
import kotlinx.html.A
import kotlinx.html.HtmlBodyTag
import kotlinx.html.a
import kotlinx.html.onClick
import kotlinx.reflection.Serialization

private val empty: JsonObject.()->Unit = {}
fun Request.jQueryPost(done: String? = null, fail: String? = null, always: String? = null, paramsBuilder:JsonObject.()->Unit = empty): String = buildString {
    val parts = requestParts()
    append("jQuery.post('")
    append(parts.first)
    append("'")

    val params = JsonObject()
    for ((key, value) in parts.second) {
        params.jsonValue(key, Serialization.serialize(value)!!)
    }
    params.paramsBuilder()

    if (!params.isEmpty()) {
        append(",")
        params.build(this)
    }
    append(")")

    if (done != null) {
        append(".done(function(data) { $done })")
    }

    if (fail != null) {
        append(".fail(function(data) { $fail })")
    }

    if (always != null) {
        append(".always(function(data) { $always })")
    }

    append(';')
}

fun HtmlBodyTag.post(link: Request, done: String? = null, fail: String? = null, always: String? = null, paramsBuilder:JsonObject.()->Unit = empty, content: A.()->Unit) {
    a {
        href="javascript:void(0);".link()

        val before = "if ($(this).hasClass('spinner')) return false; $(this).addClass('spinner');"
        val after = "$(this).removeClass('spinner');" + (always ?: "")

        onClick = before + link.jQueryPost(done ?: "location.reload()",fail, after, paramsBuilder)

        content()
    }
}
