package kara

import kara.internal.logger
import java.util.concurrent.Executors
import javax.servlet.AsyncContext
import javax.servlet.AsyncEvent
import javax.servlet.AsyncListener
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.properties.Delegates

/**
 * @author max
 */
class AsyncResult(val asyncContext: AsyncContext, val appContext: ApplicationContext, val params: RouteParameters, val body : ActionContext.() -> ActionResult) : ActionResult {
    var timed_out = false

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

private val asyncExecutors by lazy {
    Executors.newFixedThreadPool(ActionContext.tryGet()?.config?.tryGet("kara.asyncThreads")?.toInt() ?: 4)
}

private fun AsyncResult.execute() {
    try {
        if (timed_out) return

        val context = ActionContext(appContext, asyncContext.request as HttpServletRequest, asyncContext.response as HttpServletResponse, params)
        context.withContext {
            val result = context.body()

            if (!timed_out) {
                result.writeResponse(context)
            }
        }
    }
    finally {
        if (!timed_out) {
            asyncContext.complete()
        }
        else {
            logger.info("Asynchronous task timed out. See Connector's 'asyncTimeout' attribute in server.xml")
        }
    }
}

fun ActionContext.async(body: ActionContext.() -> ActionResult): ActionResult {
    val asyncContext = request.startAsync(request, response)
    val asyncResult = AsyncResult(asyncContext, appContext, params, body)

    asyncExecutors.submit {
        asyncResult.execute()
    }

    return asyncResult
}
