package kara

/**
 * If thrown during the handling of a request, a 404 response will be returned.
 */
class NotFoundException(val message : String) : RuntimeException(message) {

}


/**
 * Thrown by the form builder if an invalid model property is specified.
 */
class InvalidPropertyException(val modelClass : Class<jet.Any>, val property : String) : RuntimeException("Invalid property ${property} on type ${modelClass.getName()}") {

}
