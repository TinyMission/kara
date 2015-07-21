package kara

/**
 * If thrown during the handling of a request, a 404 response will be returned.
 */
class NotFoundException(message : String) : RuntimeException(message)
/**
 * Thrown by the form builder if an invalid model property is specified.
 */
class InvalidPropertyException(val modelClass : Class<Any>, val property : String) : RuntimeException("Invalid property ${property} on type ${modelClass.name}") {

}

/**
 * Thrown by the routing system, when it cannot handle the request
 */
class InvalidRouteException(message : String) : RuntimeException(message)

/**
 * Thrown by route handlers, that discover incorrect parameters passed
 */
public class InvalidRequestException(message: String) : RuntimeException(message)
