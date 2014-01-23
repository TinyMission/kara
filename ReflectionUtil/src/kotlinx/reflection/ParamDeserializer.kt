package kotlnx.reflection

import java.util.*
import java.net.*


/** Base class for object that deserialize parameters to a certain type.
*/
abstract class TypeSerializer() {
    abstract fun deserialize(param : String, paramType: Class<*>) : Any?
    open fun serialize(param: Any): String = param.toString()

    abstract fun isThisType(testType : Class<*>) : Boolean
}

/** Deserializer for integers.
*/
class IntSerializer() : TypeSerializer() {
    override fun deserialize(param : String, paramType: Class<*>) : Any? {
        if (param.isEmpty()) return null
        return param.toInt()
    }

    override fun isThisType(testType : Class<*>) : Boolean {
        return testType.toString() == "int" || testType.getName() == "java.lang.Integer"
    }
}

/** Deserializer for floats.
*/
class FloatSerializer() : TypeSerializer() {
    override fun deserialize(param : String, paramType: Class<*>) : Any? {
        if (param.isEmpty()) return null
        return param.toFloat()
    }

    override fun isThisType(testType : Class<*>) : Boolean {
        return testType.toString() == "float" || testType.getName() == "java.lang.Float"
    }
}

class BooleanSerializer: TypeSerializer() {
    override fun deserialize(param: String, paramType: Class<out Any?>): Any? {
        return !(param.equalsIgnoreCase("false"))
    }

    override fun isThisType(testType: Class<out Any?>): Boolean {
        return testType.getName() == "boolean" || testType.getName() == "java.lang.Boolean"
    }

    override fun serialize(param: Any): String {
        return if (param as Boolean) "true" else "false"
    }
}

public object Serialization {
    val serializer = ArrayList<TypeSerializer>();

    {
        this.loadDefaults()
    }

    public fun register(ptd : TypeSerializer) {
        serializer.add(ptd)
    }

    public fun loadDefaults() {
        register(IntSerializer())
        register(FloatSerializer())
        register(BooleanSerializer())
    }

    public fun deserialize(param : String, paramType : Class<Any>) : Any? {
        if (paramType == javaClass<String>()) {
            return param
        }
        for (deserializer in serializer) {
            if (deserializer.isThisType(paramType)) {
                return deserializer.deserialize(param, paramType)
            }
        }

        error("Can't deserialize parameter of class $paramType")
    }

    public fun serialize(param: Any?): String? {
        if (param == null) return null
        if (param is String) return param

        val paramType = param.javaClass
        for (serializer in serializer) {
            if (serializer.isThisType(paramType)) {
                return serializer.serialize(param)
            }
        }

        error("Can't serialize parameter of class $paramType")
    }
}

fun <T> Class<T>.parse(params: String) : T {
    val map = HashMap<String, String>()

    val queryComponents = params.split("\\&") map { URLDecoder.decode(it, "UTF-8") }
    for (component in queryComponents) {
        val nvp = component.split("=")
        if (nvp.size > 1)
            map[nvp[0]] = nvp[1]
        else
            map[nvp[0]] = ""
    }

    return buildBeanInstance {
        map[it]
    }
}

fun Any.serialize(): String {
    val names = LinkedHashSet(properties())
    return names.map { it to propertyValue(it) }.
    filter {it.second != null}.
    map { "${it.first}=${URLEncoder.encode(Serialization.serialize(it.second)!!, "UTF-8")}"}.
    makeString("&")

}
