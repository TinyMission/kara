package kara

import java.io.InputStream
import kotlin.html.*

public data class ResourceContent(val mime: String, val lastModified: Long, val length: Int, val data: ActionContext.() -> InputStream)

public abstract class StaticResource() : Resource() {
    abstract fun content(): ResourceContent

    override fun handle(context: ActionContext): ActionResult = content().let { BinaryResponse(it.mime, it.length, it.lastModified, it.data) }
}
