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
