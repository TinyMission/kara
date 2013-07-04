package kara

import java.io.*
import kotlin.html.*

fun Throwable.getStackTrace(): String {
    val os = ByteArrayOutputStream()
    this.printStackTrace(PrintStream(os))
    return os.toString()
}


/**
 * Layout for the standard error view.
 */
class ErrorLayout(): HtmlLayout() {

    override fun HTML.render(context: ActionContext, mainView: HtmlView) {
        head {
            title("Kara Error")

            style {
                this.body {
                    padding = box(1.em, 2.em)
                    fontFamily = "helvetica, arial, sans-serif"
                }
                id("header") {
                    padding = box(8.px, 12.px)
                    backgroundColor = c("#fff0f0")
                    border = "1px solid #fcc"
                    borderRadius = 5.px
                    marginBottom = 1.em
                    h1 {
                        marginTop = 0.px
                    }
                }
                any(id("actioninfo"), id("stacktrace")) {
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
class ErrorView(val ex: Throwable): HtmlView(ErrorLayout()) {
    override fun HtmlBodyTag.render(context: ActionContext) {
        div(id = "header") {
            h1 { +"Error Rendering Page" }
            val message = ex.getMessage()
            if (message != null)
                p { +message }
        }

        h2 { +"Action" }
        div(id = "actioninfo") {
            p {
                +"Request: ${context.request.getRequestURI()}"
            }
            p {
                +"Params: ${context.params.toString()}"
            }
            p {
                +"Session: ${context.session.getDescription()}"
            }
        }

        val stackTrace = ex.getStackTrace()
        h2 { +"Stack Trace" }
        div(id = "stacktrace") {
            +stackTrace.replace("\n", "<br/>")
        }
    }
}
