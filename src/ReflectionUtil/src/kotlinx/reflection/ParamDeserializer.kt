package kotlinx.reflection

import java.math.BigDecimal
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.*


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
        return testType.toString() == "int" || testType.name == "java.lang.Integer"
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
        return testType.toString() == "float" || testType.name == "java.lang.Float"
    }
}

class BooleanSerializer: TypeSerializer() {
    override fun deserialize(param: String, paramType: Class<out Any?>): Any? {
        return !(param.equals("false", true))
    }

    override fun isThisType(testType: Class<out Any?>): Boolean {
        return testType.name == "boolean" || testType.name == "java.lang.Boolean"
    }

    override fun serialize(param: Any): String {
        return if (param as Boolean) "true" else "false"
    }
}

class LongSerializer: TypeSerializer() {
    override fun deserialize(param : String, paramType: Class<*>) : Any? {
        if (param.isEmpty()) return null
        return param.toLong()
    }

    override fun isThisType(testType : Class<*>) : Boolean {
        return testType.toString() == "long" || testType.name == "java.lang.Long"
    }
}

class BigDecimalSerializer: TypeSerializer() {
    override fun deserialize(param : String, paramType: Class<*>) : Any? {
        if (param.isEmpty()) return null
        return BigDecimal(param)
    }

    override fun isThisType(testType : Class<*>) : Boolean {
        return BigDecimal::class.java.isAssignableFrom(testType)
    }
}

class EnumSerializer: TypeSerializer() {
    override fun serialize(param: Any): String {
        return (param as Enum<*>).ordinal().toString()
    }

    override fun deserialize(param: String, paramType: Class<*>): Any? {
        return if (paramType.isEnum) {
            paramType.getEnumConstants()?.safeGet(param.toInt())
        } else if (paramType.isEnumClass()) {
            paramType.enclosingClass.getEnumConstants()?.safeGet(param.toInt())
        }
    }

    override fun isThisType(testType: Class<out Any?>): Boolean {
        return testType.isEnum || testType.isEnumClass()
    }
}

/** Don't use multiply constructors or constructors with default values if you wish to implement this interface. **/
interface DataClass

class DataClassSerializer: TypeSerializer() {
    override fun serialize(param: Any): String {
        return param.serialize()
    }

    override fun deserialize(param: String, paramType: Class<*>): Any? {
        return paramType.parse(param)
    }

    override fun isThisType(testType: Class<out Any?>): Boolean {
        return DataClass::class.java.isAssignableFrom(testType)
    }
}

public object Serialization {
    val serializer = ArrayList<TypeSerializer>()

    init {
        this.loadDefaults()
    }

    public fun register(ptd : TypeSerializer) {
        serializer.add(ptd)
    }

    public fun loadDefaults() {
        register(IntSerializer())
        register(FloatSerializer())
        register(BooleanSerializer())
        register(LongSerializer())
        register(BigDecimalSerializer())
        register(DataClassSerializer())
        register(EnumSerializer())
    }

    public fun deserialize(param : String, paramType : Class<Any>, classLoader: ClassLoader? = null) : Any? {
        if (paramType == String::class.java) {
            return param
        }
        for (deserializer in serializer) {
            if (deserializer.isThisType(paramType) && classLoader?.let { deserializer.javaClass.classLoader in setOf(it, ClassLoader.getSystemClassLoader())}?:true) {
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

    val queryComponents = params.split("&")
    for (component in queryComponents) {
        val nvp = component.split("=")
        if (nvp.size() > 1)
            map[nvp[0]] = nvp[1]
        else
            map[nvp[0]] = ""
    }

    return buildBeanInstance(map.mapValues { urlDecode(it.value) })
}

fun Any.serialize(): String {
    val names = LinkedHashSet(primaryProperties())
    return names.map { it to propertyValue(it) }.
    filter {it.second != null}.
    map { "${it.first}=${urlEncode(Serialization.serialize(it.second)!!)}"}.
    join("&")

}

public fun urlEncode(value: String): String {
    try {
        return URLEncoder.encode(value, "UTF-8")
    }
    catch(e: Exception) {
        return value
    }
}

public fun urlDecode(encoded: String): String {
    try {
        return URLDecoder.decode(encoded, "UTF-8")
    }
    catch(e: Exception) {
        return encoded
    }
}
