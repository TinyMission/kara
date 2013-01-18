fun String.appendPathElement(part : String) : String {
    val b = StringBuilder()
    b.append(this)
    if (!this.endsWith("/")) {
        b.append("/")
    }

    if (part.startsWith('/')) {
        b.append(part.substring(1))
    }
    else {
        b.append(part)
    }

    return b.toString()
}
