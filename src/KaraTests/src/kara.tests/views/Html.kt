package kara.tests.views


import kara.link
import org.junit.Test
import kotlinx.html.a
import kotlinx.html.html
import kotlinx.html.p
import kotlin.test.assertEquals

class HtmlBuilderTest {
    @Test fun simplestBodyTest() {
        val html = html {
            p {
                +"This is text"
            }

            a {
                +"Name"
                href="xxx".link()
            }
        }

        assertEquals("<p>This is text</p><a href=\"xxx\">Name</a>", html)
    }
}
//fun testHtml(args : Array<String>) =
//    html {
//        head {
//            title {+"XML encoding with Kotlin"}
//            meta("description", "Kara html rendering")
//        }
//        body {
//            h1 {+"XML encoding with Kotlin"}
//            p {+"this format can be used as an alternative markup to XML"}
//
//            // an element with attributes and text content
//            a(href = "http://jetbrains.com/kotlin") {+"Kotlin"}
//
//            div(id="My Div") {
//                +"This is a container with an id"
//            }
//            div {
//                +"This is a container without an id"
//            }
//
//            // mixed content
//            p {
//                +"This is some"
//                b {+"mixed"}
//                +"text. For more see the"
//                a(href = "http://jetbrains.com/kotlin") {+"Kotlin"}
//                +"project"
//            }
//            p {+"some text"}
//
//            // a form
//            form(action="/", method="POST") {
//                input(inputType="text", name="some_text")
//            }
//
//            // content generated from command-line arguments
//            p {
//                +"Command line arguments were:"
//                ul {
//                    for (arg in args)
//                        li {+arg}
//                }
//            }
//        }
//    }

