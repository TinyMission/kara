package karatests.views

import kara.*
import kotlin.html.*

/** Home html view.
 */
fun HomeView() = HtmlTemplateView<DefaultTemplate>(DefaultTemplate()) {
    content {
        h2 {
            +"Welcome Home"
        }
        p {
            +"<h2>MakeSureThisIsEscaped</h2>"
        }
        p { +"<h2>MakeSureThisIsEscaped</h2>" }
    }
}
