package kara

import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.math.BigInteger
import java.security.SecureRandom
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import kotlinx.html.Link
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


fun HttpSession.getDescription() : String {
    return this.attributeNames!!.toList().map { "$it: ${this.getAttribute(it)}" }.joinToString()
}

/** This contains information about the current rendering action.
 * An action context is provided by the dispatcher to the action result when it's rendered.
 */
class ActionContext(val appContext: ApplicationContext,
                    val request : HttpServletRequest,
                    val response : HttpServletResponse,
                    val params : RouteParameters,
                    val allowHttpSession: Boolean) {
    public val config: ApplicationConfig = appContext.config
    public val session = if (allowHttpSession) HttpActionSession({request.getSession(true)!!}) else NullSession

    public val data: HashMap<Any, Any?> = HashMap()
    private val sessionCache = HashMap<String, Any?>()

    public val startedAt : Long = System.currentTimeMillis()

    fun redirect(link: Link): ActionResult {
        return RedirectResult(link.href())
    }

    fun redirect(url : String) : ActionResult {
        return RedirectResult(url.appendContext())
    }

    private fun Serializable.toBytes(): ByteArray {
        val baos = ByteArrayOutputStream()
        ObjectOutputStream(baos).writeObject(this)
        return baos.toByteArray()
    }

    private fun ByteArray.readObject(): Any? {
        return CustomClassloaderObjectInputStream(inputStream(), appContext.classLoader).readObject()
    }

    fun toSession(key: String, value: Any?) {
        if (value !is Serializable?) error("Non serializable value to session: key=$key, value=$value")
        sessionCache[key] = value
    }

    fun fromSession(key: String): Any? {
        return sessionCache.getOrPut(key) {
            val raw = session.getAttribute(key)
            when (raw) {
                is ByteArray -> raw.readObject()
                else -> raw
            }
        }
    }

    fun flushSessionCache() {
        sessionCache.forEach { entry ->
            session.setAttribute(entry.key, (entry.value as? Serializable)?.toBytes())
        }
        sessionCache.clear()
    }

    fun sessionToken(): String {
        val attr = SESSION_TOKEN_PARAMETER

        val cookie = request.cookies?.firstOrNull { it.name == attr }

        fun ActionSession.getToken() = this.getAttribute(attr) ?. let { it as String }

        return cookie?.value ?: run {
            val token = session.getToken() ?: synchronized(session.id.intern()) {
                session.getToken() ?: run {
                    val token = BigInteger(128, rnd).toString(36).take(10)
                    session.setAttribute(attr, token)
                    token
                }
            }

            if (response.getHeaders("Set-Cookie").none { it.startsWith(attr) }) {
                val newCookie = Cookie(attr, token)
                newCookie.path = "/"

                response.addCookie(newCookie)
            }
            token
        }
    }

    companion object {
        public val SESSION_TOKEN_PARAMETER: String = "_st"
        private val rnd = SecureRandom()

        val contexts = ThreadLocal<ActionContext?>()

        public fun current(): ActionContext = tryGet() ?: throw ContextException("Operation is not in context of an action, ActionContext not set.")

        fun tryGet(): ActionContext? = contexts.get()
    }
}

public class RequestScope<T:Any>(): ReadWriteProperty<Any?, T?> {
    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return ActionContext.current().data[thisRef to property] as T?
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        ActionContext.current().data.put(thisRef to property, value)
    }
}

public class LazyRequestScope<T:Any>(val initial: () -> T): ReadOnlyProperty<Any?, T> {
    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = ActionContext.current().data.getOrPut(thisRef to property, { initial() }) as T
}

public class SessionScope<T:Any>(): ReadWriteProperty<Any?, T?> {
    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return ActionContext.current().fromSession(property.name) as T?
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        ActionContext.current().toSession(property.name, value)
    }
}

public class LazySessionScope<T:Any>(val initial: () -> T): ReadOnlyProperty<Any?, T> {
    private val store = SessionScope<T>()

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return store.getValue(thisRef, property) ?: run {
            val i = initial()
            store.setValue(thisRef, property, i)
            i
        }
    }
}

public class ContextException(msg : String) : Exception(msg) {}

public fun <T> ActionContext.withContext(body: () -> T): T {
    try {
        ActionContext.contexts.set(this)
        return body()
    }
    finally {
        ActionContext.contexts.set(null)
    }
}

