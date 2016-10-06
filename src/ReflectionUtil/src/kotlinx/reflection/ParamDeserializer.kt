package kotlinx.reflection

import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.*


/** Base class for object that deserialize parameters to a certain type.
*/
abstract class TypeSerializer<T:Any?>() {
    abstract fun deserialize(param : String, paramType: Class<out T>) : T?
    open fun serialize(param: T): String = param.toString()

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

    override fun serialize(param: Boolean): String {
        return if (param) "true" else "false"
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
    override fun serialize(param: Enum<*>): String {
        return param.ordinal.toString()
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
    override fun serialize(param: DataClass): String {
        return param.serialize()
    }

    override fun deserialize(param: String, paramType: Class<out DataClass>): DataClass? {
        return paramType.parse(param)
    }

    override fun isThisType(testType: Class<*>): Boolean {
        return DataClass::class.java.isAssignableFrom(testType)
    }
}

private const val NULL_CHAR = "\u2400"
const val RECORD_SEPARATOR_CHAR = "\u001e"
@Suppress("UNCHECKED_CAST")
class ArraySerializer : TypeSerializer<Array<*>>() {
    override fun deserialize(param: String, paramType: Class<out Array<*>>): Array<*>? {
        val values = param.split(RECORD_SEPARATOR_CHAR)
        val result = java.lang.reflect.Array.newInstance(paramType.componentType, values.size) as Array<Any?>
        return result.apply {
            values.forEachIndexed { indx, e ->
                this[indx] = if (e != NULL_CHAR) {
                    Serialization.deserialize(e, paramType.componentType as Class<Any>, paramType.classLoader)
                } else {
                    null
                }
            }
        }
    }

    override fun serialize(param: Array<*>): String {
        return param.map {
            if (it == null)
                NULL_CHAR
            else {
                Serialization.serialize(it)
            }
        }.joinToString(separator = RECORD_SEPARATOR_CHAR)
    }

    override fun isThisType(testType: Class<*>): Boolean = testType.isArray
}

object Serialization {
    val serializer = ArrayList<TypeSerializer<out Any>>()
    private val logger = LoggerFactory.getLogger(this::class.java.simpleName)
    init {
        this.loadDefaults()
    }

    fun register(ptd : TypeSerializer<out Any>) {
        serializer.add(ptd)
    }

    fun loadDefaults() {
        register(IntSerializer())
        register(FloatSerializer())
        register(BooleanSerializer())
        register(LongSerializer())
        register(BigDecimalSerializer())
        register(DataClassSerializer())
        register(EnumSerializer())
        register(ArraySerializer())
    }

    fun <T:Any> deserialize(param : String, paramType : Class<T>, classLoader: ClassLoader? = null) : T? {
        if (paramType == String::class.java) {
            @Suppress("UNCHECKED_CAST")
            return param as T
        }
        if (param.contains(RECORD_SEPARATOR_CHAR) && !paramType.isArray) {
            logger.warn("Multiple parameter values: $param for non array paramType: ${paramType.canonicalName}", RuntimeException())
        }
        for (deserializer in serializer) {
            if (deserializer.isThisType(paramType) && classLoader?.let { deserializer.javaClass.classLoader in setOf(it, ClassLoader.getSystemClassLoader())}?:true) {
                @Suppress("UNCHECKED_CAST")
                return (deserializer as TypeSerializer<T>).deserialize(param, paramType)
            }
        }

        error("Can't deserialize parameter of class $paramType")
    }

    fun <T:Any> serialize(param: T?): String? {
        if (param == null) return null
        if (param is String) return param

        val paramType = param.javaClass
        for (serializer in serializer) {
            if (serializer.isThisType(paramType)) {
                @Suppress("UNCHECKED_CAST")
                return (serializer as TypeSerializer<T>).serialize(param)
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

fun urlEncode(value: String): String {
    try {
        return URLEncoder.encode(value, "UTF-8")
    }
    catch(e: Exception) {
        return value
    }
}

fun urlDecode(encoded: String): String {
    try {
        return URLDecoder.decode(encoded, "UTF-8")
    }
    catch(e: Exception) {
        return encoded
    }
}
