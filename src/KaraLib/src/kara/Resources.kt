package kara

import java.io.InputStream
import kotlin.html.*
import org.apache.commons.io.IOUtils

public data class ResourceContent(val mime: String, val lastModified: Long, val length: Int, val data: ActionContext.() -> InputStream)

public abstract class DynamicResource() : Resource() {
    abstract fun content(): ResourceContent

    override fun handle(context: ActionContext): ActionResult = content().let { BinaryResponse(it.mime, it.length, it.lastModified, it.data) }
}

public abstract class CachedResource() : DynamicResource() {
    var cache: Triple<String, ByteArray, Long>? = null

    override fun handle(context: ActionContext): ActionResult {
        val (mime, bytes, stamp) = cache ?: content().let {
            cache = with(context) { Triple(it.mime, IOUtils.toByteArray(it.data())!!, it.lastModified) }
            cache!!
        }

        return BinaryResponse(mime, bytes.size, stamp, { bytes.inputStream })
    }
}

public open class EmbeddedResource(val mime : String, val name: String) : CachedResource() {
    override fun content(): ResourceContent {
        val bytes = javaClass.getClassLoader()?.getResourceAsStream(name)?.let { IOUtils.toByteArray(it) } ?: ByteArray(0)
        return ResourceContent(mime, System.currentTimeMillis(), bytes.size) { bytes.inputStream }
    }
}

public open class Request(private val handler: ActionContext.() -> ActionResult) : Resource(){
    override fun handle(context: ActionContext): ActionResult = context.handler()
}

public open class Json(private val handler: ActionContext.() -> JsonElement) : Resource(){
    override fun handle(context: ActionContext): ActionResult = JsonResult(context.handler())
}
