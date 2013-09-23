package kara.tests.views

import kara.*
import kotlin.html.*

class DefaultTemplate : HtmlTemplate<DefaultTemplate, HTML>() {
    val content = Placeholder<BODY>()

    override fun HTML.render() {
        head {
            title {+"This is the default layout"}
        }
        body {
            h1 {+"Default Layout"}
            insert(content)
        }
    }
}


