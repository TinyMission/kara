package kotlinx.reflection

import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor


/** Base class for object that deserialize parameters to a certain type.
*/
abstract class TypeSerializer<T:Any>() {
    abstract fun deserialize(param : String, paramType: KClass<out T>) : T?
    open fun serialize(param: T): String = param.toString()

    abstract fun isThisType(testType : KClass<*>) : Boolean
}

/** Deserializer for integers.
*/
class IntSerializer() : TypeSerializer<Int>() {
    override fun deserialize(param : String, paramType: KClass<out Int>) : Int? {
        if (param.isEmpty()) return null
        return param.toInt()
    }

    override fun isThisType(testType : KClass<*>) : Boolean {
        return testType == Int::class
    }
}

/** Deserializer for floats.
*/
class FloatSerializer() : TypeSerializer<Float>() {
    override fun deserialize(param : String, paramType: KClass<out Float>) : Float? {
        if (param.isEmpty()) return null
        return param.toFloat()
    }

    override fun isThisType(testType : KClass<*>) : Boolean {
        return testType == Float::class
    }
}

class BooleanSerializer: TypeSerializer<Boolean>() {
    override fun deserialize(param: String, paramType: KClass<out Boolean>): Boolean? {
        return param.toBoolean()
    }

    override fun isThisType(testType: KClass<*>): Boolean {
        return testType == Boolean::class
    }

    override fun serialize(param: Boolean): String {
        return if (param) "true" else "false"
    }
}

class LongSerializer: TypeSerializer<Long>() {
    override fun deserialize(param : String, paramType: KClass<out Long>) : Long? {
        if (param.isEmpty()) return null
        return param.toLong()
    }

    override fun isThisType(testType : KClass<*>) : Boolean {
        return testType == Long::class
    }
}

class BigDecimalSerializer: TypeSerializer<BigDecimal>() {
    override fun deserialize(param : String, paramType: KClass<out BigDecimal>) : BigDecimal? {
        if (param.isEmpty()) return null
        return BigDecimal(param)
    }

    override fun isThisType(testType : KClass<*>) : Boolean {
        return BigDecimal::class == testType
    }
}

class EnumSerializer: TypeSerializer<Enum<*>>() {
    override fun serialize(param: Enum<*>): String {
        return param.ordinal.toString()
    }

    override fun deserialize(param: String, paramType: KClass<out Enum<*>>): Enum<*>? {
        val javaType = paramType.java
        return when {
            javaType.isEnum -> {
                javaType.enumConstants.safeGet(param.toInt())
            }
            javaType.isEnumClass() -> {
                javaType.enclosingClass.enumConstants.safeGet(param.toInt()) as Enum<*>
            }
            else -> null
        }
    }

    override fun isThisType(testType: KClass<*>): Boolean {
        val javaType = testType.java
        return javaType.isEnum || javaType.isEnumClass()
    }
}

/** Don't use multiply constructors or constructors with default values if you wish to implement this interface. **/
interface DataClass

class DataClassSerializer: TypeSerializer<DataClass>() {
    override fun serialize(param: DataClass): String {
        return param.serialize()
    }

    override fun deserialize(param: String, paramType: KClass<out DataClass>): DataClass? {
        return paramType.parse(param)
    }

    override fun isThisType(testType: KClass<*>): Boolean {
        return DataClass::class.java.isAssignableFrom(testType.java)
    }
}

private const val NULL_CHAR = "\u2400"
const val RECORD_SEPARATOR_CHAR = "\u001e"
@Suppress("UNCHECKED_CAST")
object ArraySerializer : TypeSerializer<Array<*>>() {
    override fun deserialize(param: String, paramType: KClass<out Array<*>>): Array<*>? {
        val values = param.split(RECORD_SEPARATOR_CHAR)
        val arrayType = paramType.java.componentType
        val result = java.lang.reflect.Array.newInstance(arrayType, values.size) as Array<Any?>
        return result.apply {
            values.forEachIndexed { indx, e ->
                this[indx] = if (e != NULL_CHAR) {
                    Serialization.deserialize(e, arrayType.kotlinCached, paramType.java.classLoader)
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

    override fun isThisType(testType: KClass<*>): Boolean = testType.java.isArray
}

object Serialization {
    val serializer = ArrayList<TypeSerializer<out Any>>()
    private val logger = LoggerFactory.getLogger(Serialization::class.java.simpleName)
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
        register(ArraySerializer)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T:Any> deserialize(param : String, paramType : KClass<T>, classLoader: ClassLoader? = null) : T? {
        // Temporary solution to keep backward campatibility and prevent massive crashes
        if (param.contains(RECORD_SEPARATOR_CHAR) && !paramType.java.isArray) {
            logger.error("Multiple parameter values: $param for non array paramType: ${paramType.simpleName}", RuntimeException())
            val arrayType = java.lang.reflect.Array.newInstance(paramType.java, 0)::class as KClass<Array<out T>>
            return ArraySerializer.deserialize(param, arrayType)?.firstOrNull() as T?
        }

        if (paramType == String::class) {
            @Suppress("UNCHECKED_CAST")
            return param as T
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

        val paramType = param::class
        for (serializer in serializer) {
            if (serializer.isThisType(paramType)) {
                @Suppress("UNCHECKED_CAST")
                return (serializer as TypeSerializer<T>).serialize(param)
            }
        }

        error("Can't serialize parameter of class $paramType")
    }
}

fun <T:Any> KClass<T>.parse(params: String) : T {
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
    return this::class.primaryConstructor!!.parameters.map { it.name to propertyValue<T,Any>(it.name!!) }.
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
