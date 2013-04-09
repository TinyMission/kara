package kara

import java.util.ArrayList

/** Internal object used to track middleware instances with their associated path filters. */
class MiddelwareReference(val middleware : Middleware, val filter : String) {

    /** Returns true if the filter matches the given path. */
    fun matches(path : String) : Boolean {
        if (filter.length == 0)
            return true
        return path.startsWith(filter) // TODO: make middleware filtering support wildcards
    }

}

/** Stores a list of middleware objects and their associated path filters. */
class MiddlewareList() {

    val references = ArrayList<MiddelwareReference>()

    /**
     * Adds a middleware instance with the given path filter.
     * filter is the path filter that determines if the middleware should run for a particular path.
     * By default, filter is an empty string (meaning it will run on all requests).
     */
    fun add(middleware : Middleware, filter : String = "") {
        references.add(MiddelwareReference(middleware, filter))
    }

    /** Gets all middleware references currently loaded. */
    val all : List<MiddelwareReference>
        get() = references


}
