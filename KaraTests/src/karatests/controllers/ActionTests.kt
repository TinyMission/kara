package karatests.controllers


import kara.controllers.Dispatcher
import kotlin.test.*
import javax.servlet.http.*
import karatests.mock.*
import kara.config.AppConfig


/** Tests for executing actions */
fun runActionTests() {

    var response = mockDispatch("GET", "/")
    var output = response.stringOutput()
    assertTrue(output?.contains("Default Layout") as Boolean, "Home view contains layout")
    assertTrue(output?.contains("Welcome Home") as Boolean, "Home view contains view")

    response = mockDispatch("GET", "/foo/blank")
    assertEquals("blank", response.stringOutput())

    response = mockDispatch("GET", "/foo/bar")
    assertEquals("bar", response.stringOutput())

    response = mockDispatch("GET", "/foo/bar/list")
    assertEquals("list: bar", response.stringOutput())

    response = mockDispatch("GET", "/foo/complex/bar/list/42")
    assertEquals("complex: bar id = 42", response.stringOutput())

    response = mockDispatch("GET", "/foo/redirect")
    assertEquals(HttpServletResponse.SC_MOVED_TEMPORARILY, response.getStatus())
    assertEquals("/foo/bar", response.stringOutput())

    response = mockDispatch("GET", "/crud/42")
    assertEquals("show 42", response.stringOutput())

    response = mockDispatch("GET", "/foo/compute/42/3.12")
    assertEquals("compute: 42, 3.12", response.stringOutput())

    assertEquals("/foo/compute/42/3.1415", Routes.Foo.Compute(42, 3.1415.toFloat()).toExternalForm())

    response = mockDispatch("GET", "/foo/compute?anInt=42&aFloat=3.12")
    assertEquals("compute: 42, 3.12", response.stringOutput())

    assertEquals("/foo/compute?anInt=42&aFloat=3.1415", Routes.Foo.ComputeQuery(42, 3.1415.toFloat()).toExternalForm())
}
