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

    fun toString() : String {
        val element = CssElement()
        element.render()
        val builder = StringBuilder()
        for (child in element.children) {
            child.build(builder, "")
        }
        return builder.toString()
    }

    // TODO: Implement caching and lastModified
    override fun content(): ResourceContent {
        val bytes = toString().toByteArray("UTF-8")
        return ResourceContent(-1.toLong(), bytes.size, {bytes.inputStream})
    }
}
