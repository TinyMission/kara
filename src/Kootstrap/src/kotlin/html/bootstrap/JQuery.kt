package kotlin.html.bootstrap

import kotlin.html.*
import kara.*
import java.net.URL

private val empty: JsonObject.()->Unit = {}
public fun Request.jQueryPost(done: String? = null, fail: String? = null, always: String? = null, paramsBuilder:JsonObject.()->Unit = empty): String {
    val parts = requestParts()

    val answer = StringBuilder()
    answer.append("$.post('")
    answer.append(parts.first)
    answer.append("'")

    if (parts.second.size() > 0) {
        answer.append(",")
        val params = JsonObject()
        for ((key, value) in parts.second) {
            params.jsonValue(key, ParamSerializer.serialize(value)!!)
        }
        params.paramsBuilder()

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

public fun HtmlBodyTag.post(link: Request, done: String? = null, content: A.()->Unit) {
    a {
        href="#".link()
        onClick = link.jQueryPost(done = done ?: "location.reload()")

        content()
    }
}
