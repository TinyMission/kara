package kara.views

import java.io.*
import kara.util.*
import kara.styles.*


fun Throwable.getStackTrace() : String {
    val os = ByteArrayOutputStream()
    this.printStackTrace(PrintStream(os))
    return os.toString()!!
}


/**
 * Layout for the standard error view.
 */
class ErrorLayout() : HtmlLayout() {

    override fun render(context: ActionContext, mainView: HtmlView) {
        head {
            title("Kara Error")
            style {
                s("body") {
                    padding = box(1.em, 2.em)
                    fontFamily = "helvetica, arial, sans-serif"
                }
                s("#header") {
                    padding = box(8.px, 12.px)
                    backgroundColor = c("#fff0f0")
                    border = "1px solid #fcc"
                    borderRadius = 5.px
                    marginBottom = 1.em
                    s("h1") {
                        marginTop = 0.px
                    }
                }
                s("#actioninfo, #stacktrace") {
                    padding = box(8.px, 12.px)
                    backgroundColor = c("#f0f0f0")
                    border = "1px solid #ccc"
                    borderRadius = 5.px
                    fontFamily = "courier, mono"
                    lineHeight = 1.4.em
                    fontSize = 14.px
                    overflow = Overflow.scroll
                }
            }
        }
        body {
            renderView(context, mainView)
        }
    }
}


/**
 * The standard Kara error page.
 */
class ErrorView(val ex : Throwable) : HtmlView() {
    override fun render(context: ActionContext) {
        context.session.setAttribute("hello", "world")

        div(id="header") {
            h1("Error Rendering Page")
            val message = ex.getMessage()
            if (message != null)
                p(message)
        }

        h2("Action")
        div(id="actioninfo") {
            p {
                text = "Request: ${context.request.getRequestURI()}"
            }
            p {
                text = "Params: ${context.params.toString()}"
            }
            p {
                text = "Session: ${context.session.getDescription()}"
            }
        }

        val stackTrace = ex.getStackTrace()
        h2("Stack Trace")
        div(id="stacktrace") {
            text = stackTrace.replace("\n", "<br/>")
        }
    }
}
