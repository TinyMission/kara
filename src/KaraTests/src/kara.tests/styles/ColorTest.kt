package kara.tests.styles

import org.junit.Test
import kotlinx.html.color
import kotlin.test.assertEquals

class ColorTest {
    @Test fun colors() {
        assertEquals("#888888", color("#888").toString())
        assertEquals("rgba(170, 187, 204, 0.867)", color("#abcd").toString())
        assertEquals("#101010", color("#101010").toString())
        assertEquals("#070707", color("#070707").toString())
        assertEquals("rgba(202, 254, 186, 0.745)", color("#cafebabe").toString())
    }
}
