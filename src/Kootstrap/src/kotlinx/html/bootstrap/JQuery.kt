package kotlinx.html.bootstrap

import kotlinx.html.*
import kara.*
import kotlinx.reflection.*

private val empty: JsonObject.()->Unit = {}
public fun Request.jQueryPost(done: String? = null, fail: String? = null, always: String? = null, paramsBuilder:JsonObject.()->Unit = empty): String {
    val parts = requestParts()

    val answer = StringBuilder()
    answer.append("jQuery.post('")
    answer.append(parts.first)
    answer.append("'")

    val params = JsonObject()
    for ((key, value) in parts.second) {
        params.jsonValue(key, Serialization.serialize(value)!!)
    }
    params.paramsBuilder()

    if (!params.isEmpty()) {
        answer.append(",")
        params.build(answer)
    }
    answer.append(")")

    if (done != null) {
        answer.append(".done(function(data) { $done })")
    }

    if (fail != null) {
        answer.append(".fail(function(data) { $fail })")
    }

    if (always != null) {
        answer.append(".always(function(data) { $always })")
    }

    answer.append(';')

    return answer.toString()
}

public fun HtmlBodyTag.post(link: Request, done: String? = null, fail: String? = null, always: String? = null, paramsBuilder:JsonObject.()->Unit = empty, content: A.()->Unit) {
    a {
        href="javascript:void(0);".link()

        val before = "if ($(this).hasClass('spinner')) return false; $(this).addClass('spinner');"
        val after = "$(this).removeClass('spinner');" + (always ?: "")

        onClick = before + link.jQueryPost(done ?: "location.reload()",fail, after, paramsBuilder)

        content()
    }
}
