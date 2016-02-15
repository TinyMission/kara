package kara.tests.styles

import kara.tests.styles.SampleClasses.c1
import kara.tests.styles.SampleClasses.c2
import org.junit.Test
import kotlinx.html.CssElement
import kotlinx.html.Selector
import kotlinx.html.StyleClass
import kotlin.test.assertEquals

enum class SampleClasses : StyleClass {
    c1, c2, c3
}

class SelectorsTest {
    private var result : String = "";

    private fun test(expectation: String, selector: CssElement.() -> Selector) {
        assertEquals(expectation, CssElement().selector().toExternalForm())
    }


    private fun Selector.print() {
        result = toExternalForm()
    }

    @Test fun simpleTags() {
        test("td",  { td })
        test("div", { div })
        test("*",   { any })
    }


    @Test fun simpleClasses() {
        test(".c1", { c1 })
        test(".c2", { c2 })
    }

/* Uncomment when id(String) returns IdSelector, rather than SelectorTrait
    @Test fun simpleIds() {
        test("#id", { id("id") })
    }
*/

    @Test fun tagWithIds() {
        test("div#id", { div.id("id") })
    }

    @Test fun tagWithClasses() {
        test("div.c1", {div.c(c1) })
        test("div.c1.c2", {div(c1, c2) })
    }

    @Test fun idWithClasses() {
        test("#id.c1", {any (id("id"), c1)})
        test("#id.c1.c2", {any(id("id"), c1, c2)})
    }

    @Test fun attributeFilters() {
        test("div[important]", {div(att("important"))})
        test("div[class*=\"head\"]", {div(att("class") contains "head") })
        test("div[class~=\"header\"]", {div(att("class") containsInSpaces "header") })
        test("div[lang|=\"en\"]", {div(att("lang") containsInHypen "en") })
        test("input[type=\"reset\"]", {input(att("type") equalTo "reset") })
        test("input[type^=\"re\"]", {input(att("type") startsWith "re") })
        test("input[type$=\"set\"]", {input(att("type") endsWith "set") })
    }

    @Test fun compositeSelector() {
        test("(div#id,.c1)", { forAny(div.id("id"), c1) })
    }
}
