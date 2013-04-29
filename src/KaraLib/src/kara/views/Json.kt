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


class JsonArray {
    val elements = ArrayList<Any>()
    fun value(value: ()->String) {
        elements.add(value())
    }

    fun item(body: JsonObject.()->Unit) {
        val obj = JsonObject()
        obj.body()
        elements.add(obj)
    }

    fun build(builder: StringBuilder) {
        builder.append("[")
        var first = true
        for(item in elements) {
            if (!first)
                builder.append(",")
            when (item) {
                is String -> builder.append("\"${item}\"")
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

    fun array(body: JsonArray.()->Unit): JsonArray {
        val instance = JsonArray()
        instance.body()
        return instance
    }

    fun String.to(value: String) {
        properties.put(this, value)
    }

    fun String.to(value: JsonArray) {
        properties.put(this, value)
    }

    fun String.to(body: JsonObject.()->Unit) {
        val value = JsonObject()
        value.body()
        properties.put(this, value)
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
