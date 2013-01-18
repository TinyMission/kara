package kara.controllers

fun String.routeComps() : List<RouteComp> {
    return this.split("/").map {
        it -> RouteComp.create(it)
    }
}

/** Base class for objects that represent a single component of a route. */
abstract class RouteComp(val compString : String) {
    class object {
        fun create(compString : String) : RouteComp {
            if (compString.length > 1 && compString.charAt(0) == ':' && compString.lastIndexOf(':') == 0)
                return ParamRouteComp(compString)
            else if (compString == "*")
                return WildcardRouteComp(compString)
            else
                return StringRouteComp(compString)
        }
    }

    abstract fun matches(comp : String) : Boolean

    abstract fun getParam(params : RouteParams, comp : String)
}

/** Route component for a literal string. */
class StringRouteComp(compString : String) : RouteComp(compString) {
    override fun matches(comp : String) : Boolean {
        return comp.equalsIgnoreCase(compString)
    }

    override fun getParam(params : RouteParams, comp : String) {
    }
}

/** Route component for a named parameter. */
class ParamRouteComp(compString : String) : RouteComp(compString) {
    val name = compString.substring(1)

    override fun matches(comp : String) : Boolean {
        return true
    }

    override fun getParam(params : RouteParams, comp : String) {
        params[name] = comp
    }
}

/** Route component for an unnamed parameter. */
class WildcardRouteComp(compString : String) : RouteComp(compString) {

    override fun matches(comp : String) : Boolean {
        return true
    }

    override fun getParam(params : RouteParams, comp : String) {
        params.append(comp)
    }
}
