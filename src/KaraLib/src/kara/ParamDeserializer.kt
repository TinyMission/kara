package kara

import java.util.*


/** Base class for object that deserialize parameters to a certain type.
*/
abstract class ParamTypeDeserializer() {
    abstract fun deserialize(param : String) : Any?
    open fun serialize(param: Any): String = param.toString()

    abstract fun isThisType(testType : Class<*>) : Boolean
}

/** Deserializer for integers.
*/
class IntParamDeserializer() : ParamTypeDeserializer() {
    override fun deserialize(param : String) : Any? {
        if (param.isEmpty()) return null
        return param.toInt()
    }

    override fun isThisType(testType : Class<*>) : Boolean {
        return testType.toString() == "int" || testType.getName() == "java.lang.Integer"
    }
}

/** Deserializer for floats.
*/
class FloatParamDeserializer() : ParamTypeDeserializer() {
    override fun deserialize(param : String) : Any? {
        if (param.isEmpty()) return null
        return param.toFloat()
    }

    override fun isThisType(testType : Class<*>) : Boolean {
        return testType.toString() == "float" || testType.getName() == "java.lang.Float"
    }
}

/** Deserializes string parameters into other for consumtion by actions.
*/
public object ParamSerializer {
    val _typeDeserializers = ArrayList<ParamTypeDeserializer>();

    {
        this.loadDefaults()
    }

    public fun register(ptd : ParamTypeDeserializer) {
        _typeDeserializers.add(ptd)
    }

    public fun loadDefaults() {
        register(IntParamDeserializer())
        register(FloatParamDeserializer())
    }

    public fun deserialize(param : String, paramType : Class<Any>) : Any? {
        if (param is String) {
            return param
        }
        for (deserializer in _typeDeserializers) {
            if (deserializer.isThisType(paramType)) {
                return deserializer.deserialize(param)
            }
        }

        throw RuntimeException("Can't deserialize parameter of class $paramType")
    }

    public fun serialize(param: Any?): String? {
        if (param == null) return null
        if (param is String) return param

        val paramType = param.javaClass
        for (serializer in _typeDeserializers) {
            if (serializer.isThisType(paramType)) {
                return serializer.serialize(param)
            }
        }

        throw RuntimeException("Can't serialize parameter of class $paramType")
    }
}
