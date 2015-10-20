package kotlinx.reflection

import java.math.BigDecimal
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.*


/** Base class for object that deserialize parameters to a certain type.
*/
abstract class TypeSerializer<T:Any?>() {
    abstract fun deserialize(param : String, paramType: Class<out T>) : T?
    open fun serialize(param: Any): String = param.toString()

    abstract fun isThisType(testType : Class<*>) : Boolean
}

/** Deserializer for integers.
*/
class IntSerializer() : TypeSerializer<Int>() {
    override fun deserialize(param : String, paramType: Class<out Int>) : Int? {
        if (param.isEmpty()) return null
        return param.toInt()
    }

    override fun isThisType(testType : Class<*>) : Boolean {
        return testType.toString() == "int" || testType.name == "java.lang.Integer"
    }
}

/** Deserializer for floats.
*/
class FloatSerializer() : TypeSerializer<Float>() {
    override fun deserialize(param : String, paramType: Class<out Float>) : Float? {
        if (param.isEmpty()) return null
        return param.toFloat()
    }

    override fun isThisType(testType : Class<*>) : Boolean {
        return testType.toString() == "float" || testType.name == "java.lang.Float"
    }
}

class BooleanSerializer: TypeSerializer<Boolean>() {
    override fun deserialize(param: String, paramType: Class<out Boolean>): Boolean? {
        return !(param.equals("false", true))
    }

    override fun isThisType(testType: Class<*>): Boolean {
        return testType.name == "boolean" || testType.name == "java.lang.Boolean"
    }

    override fun serialize(param: Any): String {
        return if (param as Boolean) "true" else "false"
    }
}

class LongSerializer: TypeSerializer<Long>() {
    override fun deserialize(param : String, paramType: Class<out Long>) : Long? {
        if (param.isEmpty()) return null
        return param.toLong()
    }

    override fun isThisType(testType : Class<*>) : Boolean {
        return testType.toString() == "long" || testType.name == "java.lang.Long"
    }
}

class BigDecimalSerializer: TypeSerializer<BigDecimal>() {
    override fun deserialize(param : String, paramType: Class<out BigDecimal>) : BigDecimal? {
        if (param.isEmpty()) return null
        return BigDecimal(param)
    }

    override fun isThisType(testType : Class<*>) : Boolean {
        return BigDecimal::class.java.isAssignableFrom(testType)
    }
}

class EnumSerializer: TypeSerializer<Enum<*>>() {
    override fun serialize(param: Any): String {
        return (param as Enum<*>).ordinal.toString()
    }

    @Suppress("IMPLICIT_CAST_TO_UNIT_OR_ANY")
    override fun deserialize(param: String, paramType: Class<out Enum<*>>): Enum<*>? {
        return when {
            paramType.isEnum -> {
                paramType.enumConstants.safeGet(param.toInt())
            }
            paramType.isEnumClass() -> {
                paramType.enclosingClass.enumConstants.safeGet(param.toInt()) as Enum<*>
            }
            else -> null
        }
    }

    override fun isThisType(testType: Class<*>): Boolean {
        return testType.isEnum || testType.isEnumClass()
    }
}

/** Don't use multiply constructors or constructors with default values if you wish to implement this interface. **/
interface DataClass

class DataClassSerializer: TypeSerializer<DataClass>() {
    override fun serialize(param: Any): String {
        return param.serialize()
    }

    override fun deserialize(param: String, paramType: Class<out DataClass>): DataClass? {
        return paramType.parse(param)
    }

    override fun isThisType(testType: Class<*>): Boolean {
        return DataClass::class.java.isAssignableFrom(testType)
    }
}

public object Serialization {
    val serializer = ArrayList<TypeSerializer<out Any>>()

    init {
        this.loadDefaults()
    }

    public fun register(ptd : TypeSerializer<out Any>) {
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

fun <T:Any> Class<T>.parse(params: String) : T {
    val map = HashMap<String, String>()

    val queryComponents = params.split("&")
    for (component in queryComponents) {
        val nvp = component.split("=")
        if (nvp.size > 1)
            map[nvp[0]] = nvp[1]
        else
            map[nvp[0]] = ""
    }

    return buildBeanInstance(map.mapValues { urlDecode(it.value) })
}

fun <T:Any> T.serialize(): String {
    return primaryParametersNames().map { it to propertyValue<T,Any>(it) }.
            filter {it.second != null}.
            map { "${it.first}=${urlEncode(Serialization.serialize(it.second)!!)}"}.
            joinToString("&")

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
