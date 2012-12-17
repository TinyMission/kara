package kara.helpers

import java.util.regex.Pattern

val camelPattern = Pattern.compile("(?<=[A-Z])(?=[A-Z][a-z])|(?<=[^A-Z])(?=[A-Z])|(?<=[A-Za-z])(?=[^A-Za-z])")

/**
 * Decamelizes the string. i.e. "myCamelString".decamel() -> "my camel string"
 */
fun String.decamel() : String {
    return camelPattern.matcher(this).replaceAll(" ")
}
