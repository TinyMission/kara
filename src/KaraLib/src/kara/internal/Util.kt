package kara.internal

import java.util.*

/** Maps a list by applying a function to each element. */
fun <T, R> List<T>.map(transform : (T) -> R) : List<R> {
    val result = ArrayList<R>()
    for (item in this)
        result.add(transform(item))
    return result
}


