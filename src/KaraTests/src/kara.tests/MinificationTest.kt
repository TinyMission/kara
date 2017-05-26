package kara.tests

import kara.ClosureCompiler
import org.junit.Test
import kotlin.test.assertEquals

class MinificationTest {
    @Test fun jsMinification() {
        val originalBytes = "var a = 1 + 2; console.log(a)".toByteArray()
        assertEquals("var a=3;console.log(a);", ClosureCompiler.compile(originalBytes, null).toString(Charsets.UTF_8))
    }
}