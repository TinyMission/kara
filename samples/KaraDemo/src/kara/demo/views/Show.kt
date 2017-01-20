package kara.demo.views

import kara.HtmlTemplateView
import kara.demo.models.Post
import kara.demo.styles.StyleClasses.date
import kotlinx.html.div
import kotlinx.html.h3
import kotlinx.html.label

fun Show(post: Post) = HtmlTemplateView(DetailsTemplate()) {
    caption {
        h3 { +"Show Post" }
        div{
            setClass(date)
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
