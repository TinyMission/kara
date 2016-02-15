package kara.demo.styles

import kara.Get
import kara.Stylesheet
import kara.demo.styles.StyleClasses.cLabel
import kara.demo.styles.StyleClasses.fields
import kara.demo.styles.StyleClasses.top
import kotlinx.html.*

/** The default stylesheet for the demo application.
 */
@Get("/dynamic/default.css")
object DefaultStyles : Stylesheet() {

    override fun CssElement.render() {
        body {
            backgroundColor = c("#f0f0f0")
        }

        id("main") {
            width = 85.percent
            backgroundColor = c("#fff")
            margin = box(0.px, auto)
            padding = box(1.em)
            border = "1px solid #ccc"
            borderRadius = 5.px
        }

        forAny(input(att("type") equalTo "text"), textarea) {
            padding = box(4.px)
            width = 300.px
        }

        textarea {
            height = 80.px
        }

        table(fields) {
            td {
                padding = box(6.px, 3.px)
            }
            td.c(cLabel) {
                textAlign = TextAlign.right
            }
            td(cLabel, top) {
                verticalAlign = VerticalAlign.top
            }
        }
    }
}
