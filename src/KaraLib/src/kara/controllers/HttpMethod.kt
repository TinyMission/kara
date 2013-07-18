package kara

public enum class HttpMethod {
    GET
    POST
    PUT
    DELETE

    // WebDav
    OPTIONS
    PROPFIND
    REPORT
}

fun String.asHttpMethod(): HttpMethod = when (this) {
    "GET" -> HttpMethod.GET
    "POST" -> HttpMethod.POST
    "PUT" -> HttpMethod.PUT
    "DELETE" -> HttpMethod.DELETE
    "OPTIONS" -> HttpMethod.OPTIONS
    "PROPFIND" -> HttpMethod.PROPFIND
    "REPORT" -> HttpMethod.REPORT
    else -> throw RuntimeException("Unknown $this as HTTP method")
}
