package kara

import javax.servlet.http.HttpSession

/**
 * @author max
 */
public interface ActionSession {
    val id: String

    fun getAttribute(key: String): Any?
    fun setAttribute(key: String, value: Any?)
    fun invalidate()
}

public object NullSession : ActionSession {
    override val id = "none"
    override fun getAttribute(key: String): Any? = null
    override fun setAttribute(key: String, value: Any?) {}
    override fun invalidate() {}
}

public class HttpActionSession(val s: () -> HttpSession): ActionSession {
    val session by lazy { s() }
    override val id: String get() = session.id
    override fun getAttribute(key: String): Any? = session.getAttribute(key)
    override fun setAttribute(key: String, value: Any?) {
        session.setAttribute(key, value)
    }

    override fun invalidate() {
        session.invalidate()
    }
}
