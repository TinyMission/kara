package kara

import java.io.InputStream
import kotlin.html.*
import org.apache.commons.io.IOUtils
import java.util.*

public data class ResourceContent(val mime: String, val lastModified: Long, val length: Int, val data: ActionContext.() -> InputStream) {
    public constructor(mime: String, bytes: ByteArray) : this(mime, 0, bytes.size(), {bytes.inputStream})
}

public abstract class DynamicResource() : Resource() {
    abstract fun content(context: ActionContext): ResourceContent

    override fun handle(context: ActionContext): ActionResult {
        return content(context).let { BinaryResponse(it.mime, it.length, it.lastModified, null, it.data) }
    }
}

private data class ResourceCache(val mime: String, val bytes: ByteArray, val lastModified: Long, val appVersion: Int) {
    val contentHash = Integer.toHexString(Arrays.hashCode(bytes))
}

public abstract class CachedResource() : DynamicResource() {
    var cache: ResourceCache? = null

    override fun href(): String {
        return super.href() + "?v=${hash()}"
    }

    override fun handle(context: ActionContext): ActionResult {
        if (cache?.appVersion != context.application.version) {
            cache = null
        }

        val result = ensureCachedResource(context)

        return BinaryResponse(result.mime, result.bytes.size(), result.lastModified, result.contentHash, { result.bytes.inputStream })
    }

    fun hash() : String = ensureCachedResource(ActionContext.current()).contentHash

    private fun ensureCachedResource(context: ActionContext): ResourceCache {
        return cache ?: content(context).let {
            cache = with(context) { ResourceCache(it.mime, IOUtils.toByteArray(it.data())!!.minifyResource(context, it.mime), it.lastModified, context.application.version) }
            cache!!
        }
    }
}

public open class EmbeddedResource(val mime : String, val name: String) : CachedResource() {
    override fun content(context: ActionContext): ResourceContent {
        return ResourceContent(mime, context.loadResource(name))
    }
}

public fun ActionContext.resourceStream(name: String): InputStream? {
    return application.classLoader.getResourceAsStream(name) ?: request.getServletContext()?.getResourceAsStream(name)
}

public fun ActionContext.loadResource(name: String): ByteArray {
    val stream = resourceStream(name)  ?: error("Cannot find $name in classpath or servlet context resources")
    return stream.readBytes()
}

public open class Request(private val handler: ActionContext.() -> ActionResult) : Resource(){
    override fun handle(context: ActionContext): ActionResult = context.handler()
}

public open class Json(private val handler: ActionContext.() -> JsonElement) : Resource(){
    override fun handle(context: ActionContext): ActionResult = JsonResult(context.handler())
}
