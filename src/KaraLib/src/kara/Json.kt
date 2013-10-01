package kara

import javax.servlet.http.HttpServletResponse
import org.codehaus.jackson.map.ObjectMapper
import java.util.*
import org.codehaus.jackson.map.annotate.JsonRootName

/** JSON Action Result.
 */
class JsonResult(val json: JsonElement) : ActionResult {
    override fun writeResponse(context: ActionContext) {
        val out = context.response.getWriter()
        val result = StringBuilder()
        json.build(result)
        out?.print(result.toString())
        out?.flush()
    }
}

fun jsonString(value: String): JsonValue = JsonValue(value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r\n", "\n").replace("\n", "\\n"))

trait JsonElement {
    fun build(builder: StringBuilder)
}

class JsonValue(val value: Any) : JsonElement {
    override fun build(builder: StringBuilder) {
        when (value) {
            is Int -> builder.append(value)
            is Long -> builder.append(value)
            is Boolean -> builder.append(value)
            else -> builder.append("\"${value}\"")
        }
    }
}

class JsonRoot : JsonElement {
    private var _element : JsonElement? = null
    fun set(element : JsonElement) { _element = element }
    override fun build(builder: StringBuilder) = _element?.build(builder)
}

class JsonArray : JsonElement {
    private val elements = ArrayList<JsonElement>()

    fun add(value: JsonElement) = elements.add(value)

    override fun build(builder: StringBuilder) {
        builder.append("[")
        var first = true
        for(item in elements) {
            if (!first)
                builder.append(",")
            item.build(builder)
            first = false
        }
        builder.append("]")
    }
}

class JsonObject : JsonElement {
    private val properties = HashMap<String, JsonElement>()
    fun put(name: String, value: JsonElement) = properties.put(name, value)

    override fun build(builder: StringBuilder) {
        builder.append("{")
        var first = true
        for((key, value) in properties) {
            if (!first)
                builder.append(",")
            builder.append("\"${key}\"")
            builder.append(":")
            value.build(builder)
            first = false
        }
        builder.append("}")
    }
}

fun JsonArray.jsonValue(value: String) = add(jsonString(value))
fun JsonArray.jsonValue(value: Number) = add(JsonValue(value))
fun JsonArray.jsonValue(value: Boolean) = add(JsonValue(value))
fun JsonObject.jsonValue(name: String, value: String) = put(name, jsonString(value))
fun JsonObject.jsonValue(name: String, value: Number) = put(name, JsonValue(value))
fun JsonObject.jsonValue(name: String, value: Boolean) = put(name, JsonValue(value))

fun JsonArray.jsonObject(body: JsonObject.() -> Unit) {
    val value = JsonObject()
    value.body()
    add(value)
}

fun JsonArray.jsonArray(body: JsonArray.() -> Unit) {
    val value = JsonArray()
    value.body()
    add(value)
}
fun JsonObject.jsonObject(name: String, body: JsonObject.() -> Unit) {
    val value = JsonObject()
    value.body()
    put(name, value)
}

fun JsonObject.jsonArray(name: String, body: JsonArray.() -> Unit) {
    val value = JsonArray()
    value.body()
    put(name, value)
}

fun JsonRoot.jsonArray(body: JsonArray.() -> Unit) {
    val array = JsonArray()
    array.body()
    set(array)
}

fun JsonRoot.jsonObject(body: JsonObject.() -> Unit){
    val obj = JsonObject()
    obj.body()
    set(obj)
}

fun jsonArray(body: JsonArray.() -> Unit) : JsonElement {
    val array = JsonArray()
    array.body()
    return array
}

fun jsonObject(body: JsonObject.() -> Unit) : JsonElement {
    val obj = JsonObject()
    obj.body()
    return obj
}

fun json(body: JsonRoot.() -> Unit) : JsonResult {
    val json = JsonRoot()
    json.body()
    return JsonResult(json)
}
