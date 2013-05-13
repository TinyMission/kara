package kara

import kara.internal.*
import java.io.OutputStreamWriter
import java.io.FileOutputStream

/** A class for programmatically generating CSS stylesheets.
 */
abstract class Stylesheet(var namespace : String = "") : Resource("text/css", "css") {
    /** Subclasses should override this to actual perform the stylesheet building.
    */
    abstract fun CssElement.render()

    var cache: Pair<ByteArray, Long>? = null

    fun toString() : String {
        val element = CssElement()
        element.render()
        val builder = StringBuilder()
        for (child in element.children) {
            child.build(builder, "")
        }
        return builder.toString()
    }

    override fun content(): ResourceContent {
        val (bytes, stamp) = cache ?: run {
            cache = Pair(toString().toByteArray("UTF-8"), System.currentTimeMillis())
            cache!!
        }
        return ResourceContent(stamp, bytes.size, {bytes.inputStream})
    }
}
