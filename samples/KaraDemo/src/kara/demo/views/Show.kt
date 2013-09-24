package kara.demo.views

import kara.*
import kara.demo.models.*
import kara.demo.styles.StyleClasses.*
import kara.demo.styles.StyleClasses
import kotlin.html.*

fun Show(post: Post) = HtmlTemplateView(DetailsTemplate()) {
    caption {
        h3 { +"Show Post" }
        div(c = date) {
            label { +"Date:" }
            +"${post.date}"
        }
    }

    subject {
        +"Title: ${post.title}"
    }
    message {
        +post.body
    }

}
