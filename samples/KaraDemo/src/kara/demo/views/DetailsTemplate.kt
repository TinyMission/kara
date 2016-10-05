package kara.demo.views

import kara.Placeholder
import kara.Template
import kara.demo.styles.StyleClasses.body
import kara.demo.styles.StyleClasses.title
import kara.insert
import kotlinx.html.*

class DetailsTemplate : Template<HTML>() {
    val caption = Placeholder<HtmlBodyTag>()
    val subject = Placeholder<HtmlBodyTag>()
    val message = Placeholder<HtmlBodyTag>()

    override fun HTML.render() {
        with (DefaultTemplate()) {
            content {
                h3 {
                    insert(caption)
                }
                p {
                    addClass(title)
                    insert(subject)
                }
                div {
                    addClass(body)
                    insert(message)
                }

            }
            render()
        }
    }

}
