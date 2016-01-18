package kara

public enum class HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    OPTIONS,

    // WebDav
    PROPFIND,
    PROPPATCH,
    REPORT
}

fun String.asHttpMethod(): HttpMethod = when (this) {
    "GET", "HEAD" -> HttpMethod.GET
    "POST" -> HttpMethod.POST
    "PUT" -> HttpMethod.PUT
    "DELETE" -> HttpMethod.DELETE
    "OPTIONS" -> HttpMethod.OPTIONS
    "PROPFIND" -> HttpMethod.PROPFIND
    "PROPPATCH" -> HttpMethod.PROPPATCH
    "REPORT" -> HttpMethod.REPORT
    else -> throw UnknownHttpMethodException(this)
}

class UnknownHttpMethodException(method: String) : RuntimeException("Unknown $method as HTTP method")
