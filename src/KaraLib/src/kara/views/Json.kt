package kara

import javax.servlet.http.HttpServletResponse
import org.codehaus.jackson.map.ObjectMapper
import java.util.*

/** JSON Action Result.
 */
class Json(val obj: Any): ActionResult {
    override fun writeResponse(context: ActionContext) {
        val out = context.response.getWriter()
        val mapper = ObjectMapper()
        mapper.writeValue(out, obj)
        out?.flush()
    }
}

fun jsonReflect(obj: Any): ActionResult = Json(obj)

fun jsonQuote(value : String) : String = value.replace("\"", "\\\"").replace("\r\n", "\n").replace("\n", "\\n")

class JsonArray {
    val elements = ArrayList<Any>()

    fun jsonValue(value : String)  = elements.add(jsonQuote(value))
    fun jsonValue(value : Int) = elements.add(value)

    fun jsonObject(body: JsonObject.()->Unit) {
        val value = JsonObject()
        value.body()
        elements.add(value)
    }

    fun jsonArray(body: JsonArray.()->Unit) {
        val value = JsonArray()
        value.body()
        elements.add(value)
    }

    fun build(builder: StringBuilder) {
        builder.append("[")
        var first = true
        for(item in elements) {
            if (!first)
                builder.append(",")
            when (item) {
                is String -> builder.append("\"${item}\"")
                is Int -> builder.append("${item}")
                is JsonObject -> item.build(builder)
                is JsonArray -> item.build(builder)
                else -> throw RuntimeException("Invalid entity in Json builder")
            }
            first = false
        }
        builder.append("]")
    }
}

class JsonObject {
    val properties = HashMap<String, Any>()

    fun jsonValue(name : String, value : String) = properties.put(name, jsonQuote(value))
    fun jsonValue(name : String, value : Int) = properties.put(name, value)
    fun jsonObject(name : String, body: JsonObject.()->Unit) {
        val value = JsonObject()
        value.body()
        properties.put(name, value)
    }

    fun jsonArray(name : String, body: JsonArray.()->Unit) {
        val value = JsonArray()
        value.body()
        properties.put(name, value)
    }

    fun build(builder: StringBuilder) {
        builder.append("{")
        var first = true
        for((key, value) in properties) {
            if (!first)
                builder.append(",")
            builder.append("\"${key}\"")
            builder.append(":")
            when (value) {
                is String -> builder.append("\"${value}\"")
                is JsonObject -> value.build(builder)
                is JsonArray -> value.build(builder)
                is Int -> builder.append(value)
                else -> throw RuntimeException("Invalid entity in Json builder")
            }
            first = false
        }
        builder.append("}")
    }
}

fun jsonArray(body: JsonArray.()->Unit): ActionResult {
    val array = JsonArray()
    array.body()
    val result = StringBuilder()
    array.build(result)
    return TextResult(result.toString())
}

fun jsonObject(body: JsonObject.()->Unit): ActionResult {
    val obj = JsonObject()
    obj.body()
    val result = StringBuilder()
    obj.build(result)
    return TextResult(result.toString())
}
