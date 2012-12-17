package karatests.styles

import kara.styles.*

/** A test stylesheet builder.
 */
class TestStylesheet() : Stylesheet() {
    override fun render() {
        s("body") {
            fontFamily = "sans-serif"
            backgroundColor = c("#fa542e")
        }
        s("#main") {
            clear = Clear.left
            width = 920.px
            s("h1") {
                lineHeight = 1.4.em
                margin = box(1.em, 0.em)
                color = c("#ff8822").lighten(0.1)
            }
            s(".box") {
                border = "1px solid #888"
            }
        }
    }

}
