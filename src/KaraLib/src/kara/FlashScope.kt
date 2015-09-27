package kara

import java.io.Serializable
import java.util.concurrent.ConcurrentHashMap
import javax.servlet.http.HttpServletRequest

class FlashScopeStorage : Serializable {
    private var current = ConcurrentHashMap<String, Any?>()
    private var next = ConcurrentHashMap<String, Any?>()

    operator private fun next() {
        current.clear()
        current = ConcurrentHashMap(next)
        next.clear()
    }

    operator fun get(key: String): Any? {
        if (next.containsKey(key)) {
            return next[key]
        }

        return current[key]
    }

    fun remove(key: String): Any? {
        if (current.containsKey(key)) {
            return current.remove(key)
        }

        return next.remove(key)
    }


    operator fun set(key: String, value: Any?) {
        put(key, value)
    }

    fun put(key: String, value: Any?): Any? {
        // create the session if it doesn't exist
        registerWithSessionIfNecessary()

        if (current.containsKey(key)) {
            current.remove(key)
        }

        if (value == null) {
            return next.remove(key)
        }

        return next.put(key, value)
    }

    private fun registerWithSessionIfNecessary() {
        val session = ActionContext.current().session
        if (session.getAttribute(flashScopeAttributeName) == null) {
            session.setAttribute(flashScopeAttributeName, this)
        }
    }

    companion object {
        private val flashScopeAttributeName = "jetprofile.FlashScope"

        public fun init(request: HttpServletRequest) {
            var res = request.getAttribute(flashScopeAttributeName) as FlashScopeStorage?

            if (res == null) {
                res = request.getSession(false)?.getAttribute(flashScopeAttributeName) as FlashScopeStorage?
            }

            res?.next();
        }

        public fun current() : FlashScopeStorage {
            val ctx = ActionContext.current()

            return ctx.request.getAttribute(flashScopeAttributeName) as FlashScopeStorage?
                    ?: ctx.session.getAttribute(flashScopeAttributeName) as FlashScopeStorage?
                    ?: FlashScopeStorage().apply {  ctx.request.setAttribute(flashScopeAttributeName, this) }
        }
    }
}
