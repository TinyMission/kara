package kara.config

import java.util.HashMap

/**
 * Base class for config classes that use the Kara JSON config system.
 */
open class Config() {
    val data = HashMap<String, String>()

    fun get(name : String) : String {
        if (data.containsKey(name))
            return data[name]!!
        throw RuntimeException("Could not find config value for key $name")
    }

    fun set(name : String, value : String) {
        data[name] = value
    }
}
