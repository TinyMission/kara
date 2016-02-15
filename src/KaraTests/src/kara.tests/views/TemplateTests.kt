package kara.tests.views

import kara.*
import kara.tests.mock.mockDispatch
import org.apache.log4j.BasicConfigurator
import org.junit.Before
import org.junit.Test
import kotlinx.html.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TemplateTests() {
    @Before fun setUp() {
        BasicConfigurator.configure()
    }

    @Test fun inlineTemplate() {
        val response = mockDispatch("GET", "/template/1")
        assertEquals("text/html", response._contentType, "Content type should be html")
    }

    @Test fun funTemplate() {
        val response = mockDispatch("GET", "/template/2")
        val output = response.stringOutput()
        assertEquals("text/html", response._contentType, "Content type should be html")
        assertTrue(output?.contains("header/div") as Boolean, "View contains header placeholder")
        assertTrue(output?.contains("content/left/span") as Boolean, "View contains content/left placeholder")
        assertTrue(output?.contains("content/right/span") as Boolean, "View contains content/right placeholder")
    }
}

class MenuTemplate : Template<HtmlBodyTag>() {
    val header = Placeholder<HtmlBodyTag>()
    val item = Placeholders<UL, LI>()

    override fun HtmlBodyTag.render() {
        div {
            insert(header)
        }
        ul {
            each(item) {
                li() {
                    insert(it)
                }
            }
        }
    }
}

class ContentTemplate : Template<BODY>() {
    val left = Placeholder<DIV>()
    val right = Placeholder<DIV>()

    override fun BODY.render() {
        div {
            insert(left)
        }
        div {
            insert(right)
        }
    }
}

class PageTemplate : Template<HTML>() {
    val header = Placeholder<BODY>()
    val content = TemplatePlaceholder<BODY, ContentTemplate>()
    val menu = TemplatePlaceholder<HtmlBodyTag, MenuTemplate>()

    override fun HTML.render() {
        head { }
        body {
            insert(MenuTemplate(), menu)
            insert(header)
            insert(ContentTemplate(), content)
        }
    }
}
fun view(view: PageTemplate.() -> Unit) = HtmlTemplateView<PageTemplate>(PageTemplate(), view)

fun SomeFunView() = view {
    menu {
        header { +"Menu" }
        item {

            +"Item 1"
        }
        item {
            +"Item 2"
        }
        item {
            +"Item 3"
        }
    }

    header {
        div { +"header/div" }
    }

    content {
        left {
            span { +"content/left/span" }
        }
        right {
            span { +"content/right/span" }
        }
    }

}



