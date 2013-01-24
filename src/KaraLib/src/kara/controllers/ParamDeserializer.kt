package kara.controllers

import java.util.HashMap
import java.util.ArrayList


/** Base class for object that deserialize parameters to a certain type.
*/
abstract class ParamTypeDeserializer() {
    abstract fun deserialize(param : String) : Any

    abstract fun isThisType(testType : Class<Any>) : Boolean
}

/** Deserializer for integers.
*/
class IntParamDeserializer() : ParamTypeDeserializer() {
    override fun deserialize(param : String) : Any {
        return param.toInt()
    }

    override fun isThisType(testType : Class<Any>) : Boolean {
        return testType.toString() == "int" || testType.getName() == "java.lang.Integer"
    }
}

/** Deserializer for floats.
*/
class FloatParamDeserializer() : ParamTypeDeserializer() {
    override fun deserialize(param : String) : Any {
        return param.toFloat()
    }

    override fun isThisType(testType : Class<Any>) : Boolean {
        return testType.toString() == "float" || testType.getName() == "java.lang.Float"
    }
}

/** Deserializes string parameters into other for consumtion by actions.
*/
class ParamDeserializer() {
    val _typeDeserializers : MutableList<ParamTypeDeserializer> = ArrayList<ParamTypeDeserializer>();

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

    public fun deserialize(param : String, paramType : Class<Any>) : Any {
        if (paramType == javaClass<String>()) {
            return param
        }
        for (deserializer in _typeDeserializers) {
            if (deserializer.isThisType(paramType)) {
                return deserializer.deserialize(param)
            }
        }
        return ""
    }
}
