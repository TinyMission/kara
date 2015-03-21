package kara

import java.io.InputStream
import kotlin.html.*
import org.apache.commons.io.IOUtils

public data class ResourceContent(val mime: String, val lastModified: Long, val length: Int, val data: ActionContext.() -> InputStream)

public abstract class DynamicResource() : Resource() {
    abstract fun content(context: ActionContext): ResourceContent

    override fun handle(context: ActionContext): ActionResult = content(context).let { BinaryResponse(it.mime, it.length, it.lastModified, it.data) }
}

private data class ResourceCache(val mime: String, val bytes: ByteArray, val lastModified: Long, val appVersion: Int)

public abstract class CachedResource() : DynamicResource() {
    var cache: ResourceCache? = null

    override fun handle(context: ActionContext): ActionResult {
        if (cache?.appVersion != context.application.version) {
            cache = null
        }

        val (mime, bytes, stamp) = cache ?: content(context).let {
            cache = with(context) { ResourceCache(it.mime, IOUtils.toByteArray(it.data())!!.minifyResource(context, it.mime), it.lastModified, context.application.version) }
            cache!!
        }

        return BinaryResponse(mime, bytes.size, stamp, { bytes.inputStream })
    }
}

public open class EmbeddedResource(val mime : String, val name: String) : CachedResource() {
    override fun content(context: ActionContext): ResourceContent {
        val bytes = context.loadResource(name)
        return ResourceContent(mime, System.currentTimeMillis(), bytes.size) { bytes.inputStream }
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
