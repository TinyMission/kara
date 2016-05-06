package kotlinx.html

enum class FormMethod() : StringEnum<FormMethod> {
    get,
    post,
    put,
    delete;

    override val value: String get() = name
}
