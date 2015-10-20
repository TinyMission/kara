package kara.internal

import java.util.regex.Pattern

val camelPattern = Pattern.compile("(?<=[A-Z])(?=[A-Z][a-z])|(?<=[^A-Z])(?=[A-Z])|(?<=[A-Za-z])(?=[^A-Za-z])")

/**
 * Decamelizes the string. i.e. "myCamelString".decamel() -> "my camel string"
 */
fun String.decamel() : String {
    return camelPattern.matcher(this).replaceAll(" ")
}

fun String.partition(char: Char) : Pair<String, String> {
    val idx = indexOf(char)

    return when {
        idx in 0 .. length - 1 -> substring(0, idx) to substring(idx + 1)
        else -> this to ""
    }
}
