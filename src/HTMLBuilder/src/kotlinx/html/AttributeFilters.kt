package kotlinx.html

public interface AttFilter {
    fun toExternalForm(): String
}

// E[foo]
class HasAttribute(val name: String) : kotlinx.html.AttFilter {
    override fun toExternalForm(): String {
        return ""
    }
}

// E[foo="bar"]
class Equals(val value: String) : kotlinx.html.AttFilter {
    override fun toExternalForm(): String {
        return "=\"$value\""
    }
}

// E[foo^="bar"]
class StartsWith(val value: String) : kotlinx.html.AttFilter {
    override fun toExternalForm(): String {
        return "^=\"$value\""
    }
}

// E[foo$="bar"]
class EndsWith(val value: String) : kotlinx.html.AttFilter {
    override fun toExternalForm(): String {
        return "$=\"$value\""
    }
}

enum class AttributeValueTokenizer {
    Substring, Hypen, Spaces
}
// Substring: E[foo*="bar"]
// Hypen:     E[foo|="bar"]
// Spaces:    E[foo~="bar"]
class Contains(val value: String, val tokenizer: kotlinx.html.AttributeValueTokenizer) : kotlinx.html.AttFilter {
    override fun toExternalForm(): String {
        return when (tokenizer) {
            kotlinx.html.AttributeValueTokenizer.Substring -> "*=\"$value\""
            kotlinx.html.AttributeValueTokenizer.Hypen -> "|=\"$value\""
            kotlinx.html.AttributeValueTokenizer.Spaces -> "~=\"$value\""
        }

    }
}

