package karatests.views

import kara.views.*

/** Home html view.
 */
class HomeView() : HtmlView() {
    override fun render(context: ActionContext) {
        h2 {+"Welcome Home"}
    }
}
