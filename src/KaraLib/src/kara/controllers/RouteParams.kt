package kara

import java.util.*
import kara.internal.*

/** Contains all of the parameters for a matched route. */
class RouteParams() {
    val _map = HashMap<String, String>()
    val _list = ArrayList<String>()

    /** Gets a named parameter by name */
    fun get(name : String) : String? {
        return _map[name]
    }

    /** Sets a named parameter */
    fun set(name : String, value : String) {
        if (!_map.containsKey(name)) // add it to the unnamed list as well, if it's not already there
            append(value)
        _map[name] = value
    }

    /** Gets an unnamed parameter by index */
    fun get(i : Int) : String? {
        return _list.get(i)
    }

    /** Sets an unnamed parameter */
    fun set(i : Int, value : String) {
        _list.set(i, value)
    }

    /** Apends an unnamed paramter */
    fun append(value : String) {
        _list.add(value)
    }

    /** Returns the total number of parameters */
    fun size() : Int {
        return _list.size()
    }

    /** Gets a hash with the nested values of the given name. */
    fun getHash(name : String) : HashMap<String,String> {
        val map = HashMap<String,String>()
        val prefix = name + "["
        for (key in _map.keySet()) {
            if (key.startsWith(prefix)) {
                var subkey = key.replace(prefix, "")
                subkey = subkey.substring(0, subkey.length()-1)
                val value = _map[key]!!
                map[subkey] = value
            }
        }
        return map
    }

    public fun toString() : String {
        val pairs = _map.iterator().map { it ->
            "${it.getKey()}: ${it.getValue()}"
        }.toArrayList()
        return pairs.join(", ")
    }
}
