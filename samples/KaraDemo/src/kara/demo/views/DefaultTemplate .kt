package kara.demo.views

import kara.*
import kara.demo.styles.DefaultStyles
import kotlinx.html.*
import kara.demo.routes.*

class DefaultTemplate : Template<HTML>() {
    val content = Placeholder<BODY>()
    override fun HTML.render() {
        head {
            title("Kara Demo Title")
            stylesheet(DefaultStyles)
            link(Home.LessResource)
        }
        body {
            h1 { +"Kara Demo Site" }
            div {
                this.id = "main"
                insert(content)
            }
            +"Kara is devloped by: "
            a {
                text = "Tiny Mission"
                href = "http://tinymission.com".link()
            }
            +" and "
            a {
                text = "JetBrains"
                href = "http://jetbrains.com".link()
            }
        }
    }
}
