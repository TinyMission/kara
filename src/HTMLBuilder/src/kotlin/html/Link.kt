package kotlin.html

import java.net.URL

public trait Link {
    fun href(): String
}

class DirectLink(val href: String) : Link {
    override fun href() = href
}

public fun String.link(): Link {
/* URL appeared costly due to storing registered protocols in synchronized HashTable
    if (!startsWith("/")) {
        URL(this) // Give a chance for MalformedUrlException to be thrown
    }
*/

    return DirectLink(this)
}
