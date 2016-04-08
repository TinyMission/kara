package kotlinx.html

open class HTML() : HtmlTag(null, "html") {

    var doctype: String = "<!DOCTYPE html>"

    override fun renderElement(builder: StringBuilder, indent: String) {
        builder.append("$doctype\n")
        super.renderElement(builder, indent)
    }
}

