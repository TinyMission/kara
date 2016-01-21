package kara.tests.views

import kara.*
import kotlinx.html.*

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
