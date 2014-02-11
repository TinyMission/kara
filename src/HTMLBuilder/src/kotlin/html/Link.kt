package kotlin.html

public trait Link {
    fun href(): String
}

class DirectLink(val href: String) : Link {
    override fun href() = href
}
