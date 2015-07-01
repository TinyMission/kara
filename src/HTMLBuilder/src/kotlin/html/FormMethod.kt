package kotlin.html

public enum class FormMethod() : StringEnum<FormMethod> {
    get,
    post,
    put,
    delete;

    override val value: String get() = name()
}
