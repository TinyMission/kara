package kara.styles

public trait AttFilter {
    fun toExternalForm() : String
}

// E[foo]
class HasAttribute(val name : String) : AttFilter {
    override fun toExternalForm() : String {
        return ""
    }
}

// E[foo="bar"]
class Equals(val value : String) : AttFilter {
    override fun toExternalForm(): String {
        return "=\"$value\""
    }
}

// E[foo^="bar"]
class StartsWith(val value : String) : AttFilter {
    override fun toExternalForm(): String {
        return "^=\"$value\""
    }
}

// E[foo$="bar"]
class EndsWith(val value : String) : AttFilter {
    override fun toExternalForm(): String {
        return "$=\"$value\""
    }
}

enum class AttributeValueTokenizer {
    Substring Hypen Spaces
}
// Substring: E[foo*="bar"]
// Hypen:     E[foo|="bar"]
// Spaces:    E[foo~="bar"]
class Contains(val value : String, val tokenizer : AttributeValueTokenizer) : AttFilter {
    override fun toExternalForm(): String {
        return when (tokenizer) {
           AttributeValueTokenizer.Substring -> "*=\"$value\""
           AttributeValueTokenizer.Hypen     -> "|=\"$value\""
           AttributeValueTokenizer.Spaces    -> "~=\"$value\""
        }

    }
}

