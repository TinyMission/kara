package kara

import kotlinx.html.HTML
import java.io.StringReader
import java.io.StringWriter
import javax.servlet.http.HttpServletResponse.SC_OK
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

/** Base class for objects that are returned from actions.
 */
interface ActionResult {

    /** Subclasses must implement this to write the action result to the HTTP response.
     */
    fun writeResponse(context: ActionContext): Unit
}

abstract class BaseActionResult(val contentType: String, val code: Int, val content: BaseActionResult.() -> String?) : ActionResult {
    override fun writeResponse(context: ActionContext) {
        context.response.let  { r ->
            r.status = code
            r.contentType = contentType
            content()?.let {
                with(r.writer) {
                    print(it)
                    flush()
                }
            }
        }
    }
}

/** Simple text result.
 */
open class TextResult(val text: String, code: Int = SC_OK) : BaseActionResult("text/plain", code, {text})

class EmptyResponse(status: Int) : BaseActionResult("text/plain", status, {null})

/** Redirect result.
 */
open class RedirectResult(val url: String) : ActionResult {
    override fun writeResponse(context: ActionContext) {
        context.response.sendRedirect(url)
    }
}

open class ErrorResult(val code: Int, val msg: String?) : ActionResult {
    override fun writeResponse(context: ActionContext) {
        context.response.sendError(code, msg)
    }
}

open class RequestAuthentication(val realm: String) : ErrorResult(401, "Not authorized") {
    override fun writeResponse(context: ActionContext) {
        context.response.addHeader("WWW-Authenticate", "Basic realm=\"$realm\"")
        context.response.sendError(code, msg)
    }
}

open class XmlResult(val xml: String, code: Int = SC_OK) : BaseActionResult("text/xml", code, { prettyFormat(xml, 2) }) {

     override fun writeResponse(context: ActionContext) {
        context.response.characterEncoding = "UTF-8"
        super.writeResponse(context)
    }

    companion object {
        fun prettyFormat(input: String, indent: Int): String {
            val xmlInput = StreamSource(StringReader(input))
            val stringWriter = StringWriter()
            val xmlOutput = StreamResult(stringWriter)
            val transformerFactory = TransformerFactory.newInstance()!!
            transformerFactory.setAttribute("indent-number", indent)
            val transformer = transformerFactory.newTransformer()!!
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.transform(xmlInput, xmlOutput)
            return xmlOutput.writer.toString()
        }
    }
}

open class HtmlTemplateView<T:Template<HTML>>(val template: T, val build: T.() -> Unit)  : BaseActionResult("text/html", SC_OK, {
    val page = HTML()
    with(template) {
        with(this) { build() }
        with(page) { render() }
    }
    page.toString()
})
