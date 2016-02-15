package kara.demo.views

import kara.*
import kotlinx.html.*

fun Index() = HtmlTemplateView<DefaultTemplate>(DefaultTemplate()) {
    content {
        p {
            +"This is the demo site for Kara. Below you'll find all of the html content that can be expressed in Kara views."
        }

        p {
            addClass("less-class")
            +"You can also use less"
        }

        h2 { +"Header 2" }
        h3 { +"Header 3" }
        h4 { +"Header 4" }
        h5 { +"Header 5" }
        p { +"Unordered List" }
        ul {
            for (i in 1..5) {
                li {
                    +"List Item $i"
                    style { backgroundColor = Color.fromRgb(200, 100, i * 50) }
                }
            }
            p { +"Ordered List" }
            ol {
                li { +"List Item 1" }
                li { +"List Item 2" }
            }
            fieldset() {
                label { +"Text Input" }
                input {
                    inputType = InputType.text
                    value = "Text"
                }
            }
        }
    }
}
