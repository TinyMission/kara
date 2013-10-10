package kara

import java.util.HashMap
import javax.naming.*

/**
 * Base class for config classes that use the Kara JSON config system.
 * Values are stored in a flat key/value map, and can be accessed like an array.
 */
open class Config() {
    public class MissingException(desc: String) : RuntimeException(desc)

    val data = HashMap<String, String>()

    /**
     * Gets the value for the given key.
     * Will raise an exception if the value isn't present. Try calling contains(key) first if you're unsure.
     */
    fun get(name: String): String {
        return tryGet(name) ?: throw MissingException("Could not find config value for key $name")
    }

    fun tryGet(name: String): String? {
        return lookupJNDI(name) ?: data[name]
    }

    /** Sets a value for the given key. */
    fun set(name: String, value: String) {
        data[name] = value
    }

    /** Returns true if the config contains a value for the given key. */
    fun contains(name: String): Boolean {
        return data.containsKey(name) || lookupJNDI(name) != null
    }

    /** Prints the entire config to a nicely formatted string. */
    fun toString(): String {
        val builder = StringBuilder()
        for (name in data.keySet()) {
            builder.append("$name: ${data[name]}\n")
        }
        return builder.toString()
    }

    private fun lookupJNDI(name: String): String? {
        try {
            val initCtx = InitialContext()
            val envCtx = initCtx.lookup("java:comp/env") as Context

            val folder = envCtx.lookup(name)
            return (folder as String)
        }
        catch(e: NamingException) {
            return null
        }
    }
}
