package kotlin.html

public enum class Wrap : StringEnum<Wrap> {
    soft, hard, off;

    override val value: String get() = name()
}
