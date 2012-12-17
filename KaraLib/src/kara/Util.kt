package kara.util

import java.util.*

/** Maps a list by applying a function to each element. */
fun <T, R> List<T>.map(transform : (T) -> R) : List<R> {
    val result = ArrayList<R>()
    for (item in this)
        result.add(transform(item))
    return result
}


/** Joins an array of strings with a separator. */
fun Array<String>.join(separator : String) : String {
    val builder = StringBuilder()
    for (i in this.indices) {
        builder.append(this[i])
        if (i < this.size-1)
            builder.append(separator)
    }
    return builder.toString()
}

/** Joins a list of strings with a separator. */
fun ArrayList<String>.join(separator : String) : String {
    val builder = StringBuilder()
    for (item in this) {
        builder.append(item)
        if (item != this.last())
            builder.append(separator)
    }
    return builder.toString()
}
