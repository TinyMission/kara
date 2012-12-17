package karademo.views.posts

import kara.views.HtmlView
import kara.views.*
import karademo.models.*

class Show(val post : Post) : HtmlView() {
    override fun render(context: ActionContext) {
        h3 {+ "Show Post"}
        div(c="date") {
            label {+ "Date:"}
            + "${post.date}"
        }
        div(c="title") {
            + "Title: ${post.title}"
        }
        div(c="body") {
            +post.body
        }
    }

}
