package kara.styles

import kara.config.AppConfig
import java.io.OutputStreamWriter
import java.io.FileOutputStream

/** A class for programmatically generating CSS stylesheets.
 */
abstract class Stylesheet(var namespace : String = "") : Element("") {

    /** Subclasses should override this to actual perform the stylesheet building.
    */
    abstract fun render()

    fun toString() : String {
        render()
        val builder = StringBuilder()
        for (val child in children) {
            child.build(builder, "")
        }
        return builder.toString()
    }

    /** Writes the stylesheet to its file. **/
    fun write() {
        val path = absolutePath
        val out = OutputStreamWriter(FileOutputStream(path))
        try {
            out.write(toString())
        }
        catch (ex : Exception) {
            println("Error writing stylesheet ${filename}: ${ex.getMessage() as String}")
        }
        finally {
            out.close()
        }
    }

    /** The name of the file generated. */
    var filename : String
        get() {
            return "${this.javaClass.getName()}.css"
        }
        set(value : String) {}

    /** The relative (server) path to the file. */
    var relativePath : String
        get() {
            return "/${AppConfig.current.stylesheetDir}/${filename}"
        }
        set(value : String) {}

    /** The absolute path to the file on the disk. */
    var absolutePath : String
        get() {
            return "${AppConfig.current.appRoot}/${AppConfig.current.publicDir}/${relativePath}"
        }
        set(value : String) {}
}
