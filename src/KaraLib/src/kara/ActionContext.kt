package kara

import javax.servlet.http.*
import kotlin.html.Link
import java.util.HashMap
import java.io.Serializable
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream


fun HttpSession.getDescription() : String {
    return this.getAttributeNames()!!.iterator().map { it ->
        "${it}: ${this.getAttribute(it)}"
    }.toArrayList().join(", ")
}

/** This contains information about the current rendering action.
 * An action context is provided by the dispatcher to the action result when it's rendered.
 */
class ActionContext(val application: ApplicationContext,
                    val request : HttpServletRequest,
                    val response : HttpServletResponse,
                    val params : RouteParameters) {
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
        return CustomClassloaderObjectInputStream(inputStream, application.classLoader).readObject()
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

    class object {
        val contexts = ThreadLocal<ActionContext?>()

        public fun current(): ActionContext {
            val context = tryGet()
            if (context == null)
                throw ContextException("Operation is not in context of an action, ActionContext not set.")
            return context
        }

        fun tryGet(): ActionContext? {
            return contexts.get()
        }
    }
}

class RequestScope<T>() {
    fun get(o : Any?, desc: kotlin.PropertyMetadata): T {
        val data = ActionContext.current().data
        return data.get(desc) as T
    }

    private fun set(o : Any?, desc: kotlin.PropertyMetadata, value: T) {
        ActionContext.current().data.put(desc, value)
    }
}


class LazyRequestScope<T:Any>(val initial: () -> T) {
    fun get(o : Any?, desc: kotlin.PropertyMetadata): T {
        val data = ActionContext.current().data
        if (!data.containsKey(desc)) {
            val eval = initial()
            data.put(desc, eval)
        }
        return data.get(desc) as T
    }
}

class ContextException(msg : String) : Exception(msg) {}

public fun <T> ActionContext.withContext(body: () -> T): T {
    try {
        ActionContext.contexts.set(this)
        return body()
    }
    finally {
        ActionContext.contexts.set(null)
    }
}

