package karatests.styles

import kotlin.test.*
import org.junit.Test


class StyleTests() {
    Test fun styles() {
        val styleString = TestStylesheet.toString()

        println(styleString)

        assertTrue(styleString.contains("font-family: sans-serif"))
        assertTrue(styleString.contains("#main h1 {"))
        assertTrue(styleString.contains("border-width: 1px"))
        assertTrue(styleString.contains("border-style: solid"))
        assertTrue(styleString.contains("border-color: #888888"))
    }
}
