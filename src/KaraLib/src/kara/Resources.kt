package kara

import java.io.InputStream
import kotlin.html.*
import org.apache.commons.io.IOUtils

public data class ResourceContent(val mime: String, val lastModified: Long, val length: Int, val data: ActionContext.() -> InputStream)

public abstract class DynamicResource() : Resource() {
    abstract fun content(context: ActionContext): ResourceContent

    override fun handle(context: ActionContext): ActionResult = content(context).let { BinaryResponse(it.mime, it.length, it.lastModified, it.data) }
}

public abstract class CachedResource() : DynamicResource() {
    var cache: Triple<String, ByteArray, Long>? = null

    override fun handle(context: ActionContext): ActionResult {
        val (mime, bytes, stamp) = cache ?: content(context).let {
            cache = with(context) { Triple(it.mime, IOUtils.toByteArray(it.data())!!, it.lastModified) }
            cache!!
        }

        return BinaryResponse(mime, bytes.size, stamp, { bytes.inputStream })
    }
}

public open class EmbeddedResource(val mime : String, val name: String) : CachedResource() {
    override fun content(context: ActionContext): ResourceContent {
        val bytes = javaClass.getClassLoader()?.getResourceAsStream(name.tryMinified(context))?.let { IOUtils.toByteArray(it) } ?: ByteArray(0)
        return ResourceContent(mime, System.currentTimeMillis(), bytes.size) { bytes.inputStream }
    }
}

fun String.tryMinified(context: ActionContext): String {
    if (context.application.application.config.isDevelopment()) return this

    return when {
        endsWith(".js") -> "${trimTrailing(".js")}.min.js"
        endsWith(".css") -> "${trimTrailing(".css")}.min.css"
        else -> this
    }
}

public open class Request(private val handler: ActionContext.() -> ActionResult) : Resource(){
    override fun handle(context: ActionContext): ActionResult = context.handler()
}

public open class Json(private val handler: ActionContext.() -> JsonElement) : Resource(){
    override fun handle(context: ActionContext): ActionResult = JsonResult(context.handler())
}
