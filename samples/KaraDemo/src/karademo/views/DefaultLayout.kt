package karademo.views

import kara.*
import karademo.styles.DefaultStyles

class DefaultLayout() : HtmlLayout() {
    override fun render(context: ActionContext, mainView: HtmlView) {
        head {
            title("Kara Demo Title")
            stylesheet(DefaultStyles())
        }
        body {
            h1("Kara Demo Site")
            div(id="main") {
                renderView(context, mainView)
            }
            a(text="Kara is developed by Tiny Mission", href="http://tinymission.com".link())
        }
    }
}
