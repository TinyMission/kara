package karademo.views

import kara.*
import karademo.styles.DefaultStyles
import kotlin.html.*

class DefaultLayout(): HtmlLayout() {
    override fun HTML.render(mainView: HtmlView) {
        head {
            title("Kara Demo Title")
            stylesheet(DefaultStyles)
        }
        body {
            h1 { +"Kara Demo Site" }
            div(id = "main") {
                renderView(mainView)
            }
            +"Kara is devloped by: "
            a {
                text = "Tiny Mission"
                href = "http://tinymission.com".link()
            }
            +" and "
            a {
                text = "JetBrains"
                href = "http://jetbrains.com".link()
            }
        }
    }
}
