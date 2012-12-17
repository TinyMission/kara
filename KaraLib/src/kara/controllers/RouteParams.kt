package kara.controllers

import java.util.*
import kara.util.*

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

    public fun toString() : String {
        val pairs = _map.iterator().map { it ->
            "${it.getKey()}: ${it.getValue()}"
        }.toArrayList()
        return pairs.join(", ")
    }
}
