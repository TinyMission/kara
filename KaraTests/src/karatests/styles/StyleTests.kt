package karatests.styles

import kotlin.test.*


fun main(args : Array<String>) {
    val ss = TestStylesheet()
    val styleString = ss.toString()

    println(styleString)

    assertTrue(styleString.contains("font-family: sans-serif"))
    assertTrue(styleString.contains("#main h1 {"))
    assertTrue(styleString.contains("border-width: 1.0px"))
    assertTrue(styleString.contains("border-style: solid"))
    assertTrue(styleString.contains("border-color: #888"))
}