package karademo.views

import kara.*
import karademo.models.*
import karademo.styles.StyleClasses.*
import karademo.styles.StyleClasses
import kotlin.html.*

fun Show(post: Post) = HtmlTemplateView<DefaultTemplate>(DefaultTemplate()) {
    content {
        h3 { +"Show Post" }
        div(c = date) {
            label { +"Date:" }
            +"${post.date}"
        }
        div(c = StyleClasses.title) {
            +"Title: ${post.title}"
        }
        div(c = body) {
            +post.body
        }
    }

}
