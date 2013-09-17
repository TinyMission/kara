package karatests.views

import kotlin.test.*
import kotlin.html.*
import kara.*
import org.junit.*
import org.apache.log4j.BasicConfigurator
import karatests.mock.*

class TemplateTests() {
    Before fun setUp() {
        BasicConfigurator.configure()
    }

    Test fun inlineTemplate() {
        val response = mockDispatch("GET", "/template/1")
        val output = response.stringOutput()
        assertEquals("text/html", response._contentType, "Content type should be html")

    }

    Test fun classTemplate() {
        val response = mockDispatch("GET", "/template/2")
        val output = response.stringOutput()
        assertEquals("text/html", response._contentType, "Content type should be html")

    }

    Test fun funTemplate() {
        val response = mockDispatch("GET", "/template/3")
        val output = response.stringOutput()
        assertEquals("text/html", response._contentType, "Content type should be html")

    }
}

class DefaultPageTemplate : HtmlTemplate<DefaultPageTemplate>() {
    val header = HtmlPlaceholder()
    val content = HtmlPlaceholder()

    override fun HTML.render(view: HtmlTemplateView<DefaultPageTemplate>) {
        head {

        }

        body {
            render(header)
            render(content)
        }
    }
}
fun view(view: DefaultPageTemplate.() -> Unit) = HtmlTemplateView<DefaultPageTemplate>(DefaultPageTemplate(), view)

fun SomeFunView() = view {
    header {
        div {

        }
    }

    content {
        span {

        }
    }

}

class SomeView() : HtmlTemplateView<DefaultPageTemplate>(DefaultPageTemplate(), {
    header {
        div {

        }
    }

    content {
        span {

        }
    }
})



