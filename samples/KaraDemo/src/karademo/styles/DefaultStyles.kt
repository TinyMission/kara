package karademo.styles

import kara.styles.*

/** The default stylesheet for the demo application.
 */
class DefaultStyles() : Stylesheet() {

    override fun render() {
        s("body") {
            backgroundColor = c("#f0f0f0")
        }
        s("#main") {
            width = 85.percent
            backgroundColor = c("#fff")
            margin = box(0.px, auto)
            padding = box(1.em)
            border = "1px solid #ccc"
            borderRadius = 5.px
        }

        s("input[type=text], textarea") {
            padding = box(4.px)
            width = 300.px
        }
        s("textarea") {
            height = 80.px
        }

        s("table.fields") {
            s("td") {
                padding = box(6.px, 3.px)
            }
            s("td.label") {
                textAlign = TextAlign.right
            }
            s("td.label.top") {
                verticalAlign = VerticalAlign.top
            }
        }
    }
}
