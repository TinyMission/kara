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
import kotlin.html.Link


fun HttpSession.getDescription() : String {
    return this.attributeNames!!.toList().map { "$it: ${this.getAttribute(it)}" }.join()
}

/** This contains information about the current rendering action.
 * An action context is provided by the dispatcher to the action result when it's rendered.
 */
class ActionContext(val appContext: ApplicationContext,
                    val request : HttpServletRequest,
                    val response : HttpServletResponse,
                    val params : RouteParameters) {
    public val config: ApplicationConfig = appContext.config
    public val session : HttpSession = request.getSession(true)!!
    public val data: HashMap<Any, Any?> = HashMap()
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
        session.setAttribute(key, (value as? Serializable)?.toBytes())
    }

    fun fromSession(key: String): Any? {
        val raw = session.getAttribute(key)
        return when (raw) {
            is ByteArray -> raw.readObject()
            else -> raw
        }
    }

    fun sessionToken(): String {
        val attr = SESSION_TOKEN_PARAMETER

        val cookie = request.cookies?.firstOrNull { it.name == attr }

        fun HttpSession.getToken() = this.getAttribute(attr) ?. let { it as String }

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

public class RequestScope<T>() {
    operator @Suppress("UNCHECKED_CAST")
    fun get(o : Any?, desc: kotlin.PropertyMetadata): T {
        val data = ActionContext.current().data
        return data.get(desc) as T
    }

    operator private fun set(o : Any?, desc: kotlin.PropertyMetadata, value: T) {
        ActionContext.current().data.put(desc, value)
    }
}


public class LazyRequestScope<T:Any>(val initial: () -> T) {
    @Suppress("UNCHECKED_CAST")
    operator fun get(o: Any?, desc: kotlin.PropertyMetadata): T = ActionContext.current().data.getOrPut(desc, { initial() }) as T
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

