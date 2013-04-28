package kara

import java.util.*
import java.io.PrintWriter
import kara.internal.*

open class HTML(): HtmlTag(null, "html") {

    public var doctype: String = "<!DOCTYPE html>"

    override fun renderElement(appConfig: AppConfig, builder: StringBuilder, indent: String) {
        builder.append("$doctype\n")
        super<HtmlTag>.renderElement(appConfig : AppConfig, builder, indent)
    }
}

