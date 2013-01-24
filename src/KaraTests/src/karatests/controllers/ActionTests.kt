package karatests.controllers


import kara.controllers.Dispatcher
import kotlin.test.*
import javax.servlet.http.*
import karatests.mock.*
import kara.config.AppConfig
import org.junit.Test
import org.junit.Before
import org.apache.log4j.BasicConfigurator


/** Tests for executing actions */
class ActionTests() {
    Before fun setUp() {
        BasicConfigurator.configure()
    }

    Test fun basicHtmlLayout() {
        val response = mockDispatch("GET", "/")
        val output = response.stringOutput()
        assertTrue(output?.contains("Default Layout") as Boolean, "Home view contains layout")
        assertTrue(output?.contains("Welcome Home") as Boolean, "Home view contains view")
    }

    Test fun runActionTests() {
        assertResponse("blank", "/foo/blank")
        assertResponse("bar", "/foo/bar")
        assertResponse("list: bar", "/foo/bar/list")
        assertResponse("complex: bar id = 42", "/foo/complex/bar/list/42")
        assertResponse("show 42", "/crud/42")
        assertResponse("compute: 42, 3.12", "/foo/compute/42/3.12")
        assertResponse("compute: 42, 3.12", "/foo/compute?anInt=42&aFloat=3.12")
    }

    Test fun redirect() {
        val response = mockDispatch("GET", "/foo/redirect")
        assertEquals(HttpServletResponse.SC_MOVED_TEMPORARILY, response.getStatus())
        assertEquals("/foo/bar", response.stringOutput())
    }

    Test fun externalForm() {
        assertEquals("/foo/compute?anInt=42&aFloat=3.1415", Routes.Foo.ComputeQuery(42, 3.1415.toFloat()).toExternalForm())
        assertEquals("/foo/compute/42/3.1415", Routes.Foo.Compute(42, 3.1415.toFloat()).toExternalForm())
    }

    fun assertResponse(expected : String, url : String) {
        val response = mockDispatch("GET", url)
        assertEquals(expected, response.stringOutput())
    }
}
