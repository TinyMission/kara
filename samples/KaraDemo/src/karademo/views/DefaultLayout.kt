package karademo.views

import kara.*
import karademo.styles.DefaultStyles

class DefaultLayout(): HtmlLayout() {
    override fun HTML.render(context: ActionContext, mainView: HtmlView) {
        head {
            title("Kara Demo Title")
            stylesheet(DefaultStyles)
        }
        body {
            h1 { +"Kara Demo Site" }
            div(id = "main") {
                renderView(context, mainView)
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
