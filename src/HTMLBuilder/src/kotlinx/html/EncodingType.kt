package kotlinx.html

public enum class EncodingType(override val value: String) : StringEnum<EncodingType> {
    urlencoded("application/x-www-form-urlencoded"),
    multipart("multipart/form-data"),
    plain("text/plain")
}
