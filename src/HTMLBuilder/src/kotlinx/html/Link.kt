package kotlinx.html

interface Link {
    fun href(): String
}

class DirectLink(val href: String) : Link {
    override fun href() = href
}
