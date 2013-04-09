package karatests.views

import kara.*
import kara.internal.*

/** A default layout implementation.
 */
class DefaultLayout() : HtmlLayout() {
    override fun render(context: ActionContext, mainView: HtmlView) {
        head {
            title {+"This is the default layout"}
        }
        body {
            h1 {+"Default Layout"}
            renderView(context, mainView)
        }
    }
}
