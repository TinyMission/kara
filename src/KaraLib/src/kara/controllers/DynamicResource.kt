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

        return BinaryResponse(mime, bytes.size, stamp, {bytes.inputStream})
    }
}
