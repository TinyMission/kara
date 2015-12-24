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
    private var _session: HttpSession? = null

    private fun getSession(): HttpSession {
        if(_session == null) {
            _session = s()
        }
        return _session!!
    }

    override val id: String get() = getSession().id
    override fun getAttribute(key: String): Any? = getSession().getAttribute(key)
    override fun setAttribute(key: String, value: Any?) {
        getSession().setAttribute(key, value)
    }

    override fun invalidate() {
        getSession().invalidate()
        _session = null
    }
}
