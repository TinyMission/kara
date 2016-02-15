package kara.tests.views

import kara.*
import kotlinx.html.*

class DefaultTemplate : Template<HTML>() {
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


