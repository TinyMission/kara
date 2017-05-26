package kara

import kara.internal.logger
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ThreadPoolExecutor
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author max
 */
class AsyncResult(val asyncContext: AsyncContext, val appContext: ApplicationContext, val params: RouteParameters, val allowHttpSession: Boolean, val body : ActionContext.() -> ActionResult) : ActionResult {
    internal val createdAt = System.currentTimeMillis()
    internal val deadline = createdAt + asyncContext.timeout

    var timed_out = false

    fun exired() = deadline < System.currentTimeMillis()

    init {
        asyncContext.addListener(object: AsyncListener {
            override fun onComplete(e: AsyncEvent) {
            }

            override fun onTimeout(e: AsyncEvent) {
                timed_out = true
            }

            override fun onStartAsync(e: AsyncEvent) {
            }

            override fun onError(e: AsyncEvent) {
            }
        })
    }

    override fun writeResponse(context: ActionContext) {
    }
}

object KaraAsyncExecutor {

    private val DEFAULT_THREADS_COUNT = 4

    private lateinit var executor: ThreadPoolExecutor
    @Volatile
    private var initialized = false

    @Synchronized
    fun initialize(config: ApplicationConfig) {
        if (initialized) return

        var idx = 1
        val threadCount = config.tryGet("kara.asyncThreads")?.toInt()
        if (threadCount == null) {
            logger.error("Async Executor will be initialized with default $DEFAULT_THREADS_COUNT threads!")
        }
        executor = Executors.newFixedThreadPool(threadCount ?: DEFAULT_THREADS_COUNT, {
            Executors.defaultThreadFactory().newThread(it).apply {
                name = "Kara async request executor - ${idx++}"
            }
        }) as ThreadPoolExecutor
        executor.prestartAllCoreThreads()
        initialized = true
    }

    fun shutdown() {
        if (initialized) executor.shutdown()
    }

    fun submit(task: ()->Unit): Future<*>? {
        if (!initialized) error("Async Executor is not initialized yet")
        return executor.submit(task)
    }

    val queueSize: Int get() = if (initialized) executor.queue.size else 0
}

private fun AsyncResult.execute() {
    try {
        if (timed_out) return

        checkExpired()

        val context = ActionContext(appContext, asyncContext.request as HttpServletRequest, asyncContext.response as HttpServletResponse, params, allowHttpSession)
        context.withContext {
            val result = context.body()

            context.flushSessionCache()
            if (!timed_out) {
                result.writeResponse(context)
            }
        }
    }
    finally {
        if (!timed_out) {
            checkExpired()
            asyncContext.complete()
        }
        else {
            logger.info("Asynchronous task timed out. See Connector's 'asyncTimeout' attribute in server.xml")
        }
    }
}

private fun AsyncResult.checkExpired() {
    if (exired()) {
        logger.warn("Asynchronous task timed out (${System.currentTimeMillis() - deadline}ms ago), but onTimeout event was not triggered.")
    }
}

fun ActionContext.async(body: ActionContext.() -> ActionResult): ActionResult {
    val asyncContext = request.startAsync(request, response)
    val asyncResult = AsyncResult(asyncContext, appContext, params, allowHttpSession, body)

    KaraAsyncExecutor.submit {
        asyncResult.execute()
    }

    return asyncResult
}
