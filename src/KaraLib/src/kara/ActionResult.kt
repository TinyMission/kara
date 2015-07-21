package kara

import javax.servlet.http.HttpServletResponse
import javax.xml.transform.*
import javax.xml.transform.stream.*
import java.io.*

/** Base class for objects that are returned from actions.
 */
interface ActionResult {

    /** Subclasses must implement this to write the action result to the HTTP response.
     */
    fun writeResponse(context: ActionContext): Unit
}


/** Simple text result.
 */
open class TextResult(val text: String) : ActionResult {
    override fun writeResponse(context: ActionContext) {
        context.response.contentType = "text/plain"
        val out = context.response.writer
        out?.print(text)
        out?.flush()
    }
}


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

open class XmlResult(val xml: String) : ActionResult {
    fun prettyFormat(input: String, indent: Int): String {
        val xmlInput = StreamSource(StringReader(input));
        val stringWriter = StringWriter();
        val xmlOutput = StreamResult(stringWriter);
        val transformerFactory = TransformerFactory.newInstance()!!;
        transformerFactory.setAttribute("indent-number", indent);
        val transformer = transformerFactory.newTransformer()!!;
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(xmlInput, xmlOutput);
        return xmlOutput.writer.toString();
    }

    override fun writeResponse(context: ActionContext) {
        context.response.status = 200
        respondWithXml(context)
    }

    fun respondWithXml(context: ActionContext) {
        val text = prettyFormat(xml, 2)
        val content = text.toByteArray();
        context.response.setContentLength(content.size())
        context.response.contentType = "text/xml"
        context.response.characterEncoding = "UTF-8"
        val out = context.response.outputStream
        out?.write(content)
        out?.flush()
    }
}
