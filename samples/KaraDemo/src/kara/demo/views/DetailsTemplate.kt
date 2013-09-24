package kara.demo.views

import kara.*
import kara.demo.styles.DefaultStyles
import kara.demo.styles.StyleClasses.*
import kotlin.html.*
import javax.swing.text.DefaultEditorKit.InsertBreakAction

class DetailsTemplate : HtmlTemplate<DetailsTemplate, HTML>() {
    val caption = Placeholder<HtmlBodyTag>()
    val subject = Placeholder<HtmlBodyTag>()
    val message = Placeholder<HtmlBodyTag>()

    override fun HTML.render() {
        with (DefaultTemplate()) {
            content {
                h3 {
                    insert(caption)
                }
                p(title) {
                    insert(subject)
                }
                div(body) {
                    insert(message)
                }

            }
            render()
        }
    }

}