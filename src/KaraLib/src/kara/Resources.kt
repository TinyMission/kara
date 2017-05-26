package kara

import org.apache.commons.io.IOUtils
import java.io.File
import java.io.InputStream
import java.net.URL
import java.util.*

data class ResourceContent(val mime: String, val lastModified: Long?, val length: Int?, val data: ActionContext.() -> InputStream) {
    constructor(mime: String, bytes: ByteArray) : this(mime, null, bytes.size, { bytes.inputStream() })
}

abstract class DynamicResource : Resource() {
    abstract fun content(context: ActionContext): ResourceContent

    override fun handle(context: ActionContext): ActionResult {
        return content(context).let { BinaryResponse(it.mime, it.length, it.lastModified, null, it.data) }
    }
}

data class ResourceCache(val mime: String, val bytes: ByteArray, val lastModified: Long?, val appVersion: Int) {
    val contentHash: String = Integer.toHexString(Arrays.hashCode(bytes))
}

abstract class CachedResource : DynamicResource() {
    @Volatile
    private var cache: ResourceCache? = null

    override fun href(): String {
        return super.href() + "?v=${versionHash()}"
    }

    open fun validateCache(context: ActionContext, cache: ResourceCache): Boolean = true

    override fun handle(context: ActionContext): ActionResult {
        val result = ensureCachedResource(context)

        return BinaryResponse(result.mime, result.bytes.size, result.lastModified, result.contentHash, { result.bytes.inputStream() })
    }

    fun versionHash() : String = ensureCachedResource(ActionContext.current()).contentHash

    private fun ensureCachedResource(context: ActionContext): ResourceCache {
        if (context.config.isDevelopment()) {
            cache?.let {
                if (it.appVersion != context.appContext.version || it.lastModified != null && !validateCache(context, it)) {
                    cache = null
                }
            }
        }

        return cache ?: synchronized(this) {
            cache ?: content(context).let {
                val resourceName = (this@CachedResource as? EmbeddedResource)?.name.orEmpty()
                cache = with(context) {
                    ResourceCache(it.mime,
                            IOUtils.toByteArray((it.data)())!!.minifyResource(context, it.mime, resourceName),
                            it.lastModified,
                            context.appContext.version) }
                cache!!
            }
        }
    }
}

open class EmbeddedResource(val mime : String, val name: String) : CachedResource() {
    override fun content(context: ActionContext): ResourceContent {
        val (modification, content) = context.loadResource(name)
        return ResourceContent(mime, modification, null, {content.openStream()})
    }

    override fun validateCache(context: ActionContext, cache: ResourceCache): Boolean {
        return context.loadResource(name).first == cache.lastModified
    }
}

fun ActionContext.resourceURL(name: String): URL? {
    return appContext.classLoader.getResource(name) ?: request.servletContext?.getResource(name)
}

fun ActionContext.publicDirectoryResource(name: String): Pair<Long?, URL>? {
    for (dir in config.publicDirectories) {
        val candidate = File(dir, name)
        if (candidate.exists()) {
            return candidate.lastModified() to candidate.toURI().toURL()
        }
    }

    return null
}

fun ActionContext.loadResource(name: String): Pair<Long?, URL> {
    return publicDirectoryResource(name) ?:
            null to (resourceURL(name) ?: throw NotFoundException("Cannot find $name in classpath or servlet context resources"))
}

open class Request(private val handler: ActionContext.() -> ActionResult) : Resource(){
    override fun handle(context: ActionContext): ActionResult = context.handler()
}

open class Json(private val handler: ActionContext.() -> JsonElement) : Resource(){
    override fun handle(context: ActionContext): ActionResult = JsonResult(context.handler())
}
