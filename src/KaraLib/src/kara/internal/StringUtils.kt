package kara.internal

import java.util.regex.Pattern

val camelPattern = Pattern.compile("(?<=[A-Z])(?=[A-Z][a-z])|(?<=[^A-Z])(?=[A-Z])|(?<=[A-Za-z])(?=[^A-Za-z])")

/**
 * Decamelizes the string. i.e. "myCamelString".decamel() -> "my camel string"
 */
fun String.decamel() : String {
    return camelPattern.matcher(this).replaceAll(" ")
}

/** Joins an array of strings with a separator. */
fun Array<String>.join(separator : String) : String {
    val builder = StringBuilder()
    for (i in indices) {
        builder.append(this[i])
        if (i < size-1)
            builder.append(separator)
    }
    return builder.toString()
}

/** Joins a list of strings with a separator. */
fun List<String>.join(separator : String) : String {
    val builder = StringBuilder()
    var first = true
    for (item in this) {
        if (!first)
            builder.append(separator)
        builder.append(item)
        first = false
    }
    return builder.toString()
}

fun String.htmlEscape() : String {
    return replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;")
}
