package kara

import java.io.InputStream
import kotlin.html.*

public data class ResourceContent(val lastModified: Long, val length: Int, val data: ActionContext.() -> InputStream)

public abstract class Resource(val mime: String, val extension: String = mime.substring(mime.lastIndexOf('/'))): Link {
    abstract fun content(): ResourceContent

    override fun href(): String {
        return "/resources/${javaClass.getName()}.${extension}"
    }
}
