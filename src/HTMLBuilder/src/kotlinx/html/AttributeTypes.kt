package kotlinx.html

import kotlin.reflect.KProperty

abstract class Attribute<T>(val name: String) {
    operator fun getValue(tag: HtmlTag, property: KProperty<*>): T {
        return decode(tag[name])
    }
    operator open fun setValue(tag: HtmlTag, property: KProperty<*>, value: T) {
        tag[name] = encode(value)
    }

    abstract fun encode(t: T): String?
    abstract fun decode(s: String?): T
}

open class StringAttribute(name: String) : Attribute<String>(name) {
    override fun encode(t: String): String? {
        return t // TODO: it actually might need HTML esaping
    }

    override fun decode(s: String?): String {
        return s!! // TODO: it actually might need decode
    }
}

class TextAttribute(name: String) : StringAttribute(name)
class RegexpAttribute(name: String) : StringAttribute(name)
class IdAttribute(name: String) : StringAttribute(name)
class MimeAttribute(name: String) : StringAttribute(name)

class IntAttribute(name: String) : Attribute<Int>(name) {
    override fun encode(t: Int): String? {
        return t.toString()
    }

    override fun decode(s: String?): Int {
        return s!!.toInt()
    }
}

class BooleanAttribute(name: String, val trueValue: String = "true", val falseValue: String = "false") : Attribute<Boolean>(name) {
    override fun encode(t: Boolean): String? {
        return if (t) trueValue else falseValue
    }

    override fun decode(s: String?): Boolean {
        return when (s) {
            trueValue -> true
            falseValue -> false
            else -> throw RuntimeException("Unknown value for $name=$s")
        }
    }
}

class TickerAttribute(name: String) : Attribute<Boolean>(name) {
    override fun encode(t: Boolean): String? {
        return null
    }

    override fun decode(s: String?): Boolean {
        return if (s == null) false else true
    }

    operator override fun setValue(tag: HtmlTag, property: KProperty<*>, value: Boolean) {
        if (value == true) {
            super.setValue(tag, property, value)
        } else {
            tag.removeAttribute(name)
        }
    }
}

class LinkAttribute(name: String) : Attribute<Link>(name) {
    override fun encode(t: Link): String? {
        return t.href()
    }

    override fun decode(s: String?): Link {
        return DirectLink(s!!)
    }
}

interface StringEnum<T : Enum<T>> {
    val value: String
}

class EnumAttribute<T>(name: String, val klass: Class<T>) : Attribute<T>(name)
    where T : StringEnum<T>, T : Enum<T>
{
    override fun encode(t: T): String? {
        return t.value
    }

    override fun decode(s: String?): T {
        return klass.enumConstants!!.firstOrNull { encode(it) == s } ?: throw RuntimeException("Can't decode '$s' as value of '${klass.name}'")
    }
}

class MimeTypesAttribute(name: String) : Attribute<List<String>>(name) {

    override fun encode(t: List<String>): String? {
        return t.joinToString(",")
    }
    override fun decode(s: String?): List<String> {
        return s!!.split(',').map { it.trim() }
    }
}
