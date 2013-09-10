package karademo.views.posts

import kara.*
import karademo.models.*
import karademo.styles.StyleClasses.*
import karademo.styles.StyleClasses
import kotlin.html.*

class Show(val post : Post) : HtmlView() {
    override fun HtmlBodyTag.render() {
        h3 {+ "Show Post"}
        div(c=date) {
            label {+ "Date:"}
            + "${post.date}"
        }
        div(c=StyleClasses.title) {
            + "Title: ${post.title}"
        }
        div(c=body) {
            +post.body
        }
    }

}
