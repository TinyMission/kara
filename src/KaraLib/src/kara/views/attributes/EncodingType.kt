package kara.views

public enum class EncodingType(override val value: String) : StringEnum<EncodingType> {
    urlencoded : EncodingType("application/x-www-form-urlencoded")
    multipart : EncodingType("multipart/form-data")
    plain : EncodingType("text/plain")
}
