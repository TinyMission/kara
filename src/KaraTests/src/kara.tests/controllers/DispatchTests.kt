package kara.tests.controllers

import kara.Application
import kara.ApplicationConfig
import kara.internal.ResourceDispatcher
import kara.internal.scanObjects
import kara.tests.mock.mockRequest
import org.apache.log4j.BasicConfigurator
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/** Tests for dispatching routes to get action info. */
class DispatchTests() {
    @Before fun setUp() {
        BasicConfigurator.configure()
    }

    @Test fun runDispatchTests() {
        val appConfig = ApplicationConfig.loadFrom("src/KaraTests/src/kara.tests/test.conf")

        val app = object : Application(appConfig) {}

        val dispatcher = ResourceDispatcher(app.context, scanObjects(arrayOf(Routes)))

        var actionInfo = dispatcher.findDescriptor("GET", "/")!!
        assertNotNull(actionInfo)

        assertNull(dispatcher.findDescriptor("GET", "/invalid_path"))

        // foo controller
        actionInfo = dispatcher.findDescriptor("GET", "/foo/bar")!!
        assertEquals(Routes.Foo.Bar().javaClass, actionInfo.resourceClass)

        dispatcher.findDescriptor("GET", "/foo/bar/baz")!! // nested route

        dispatcher.findDescriptor("GET", "/foo/foobar") // default action name

        var request = mockRequest("GET", "/foo/bar/list")
        actionInfo = dispatcher.findDescriptor("GET", request.requestURI!!)!! // unnamed param
        var params = actionInfo.buildParams(request)
        assertEquals("bar", params[0])

        request = mockRequest("GET", "/foo/complex/bar/list/42")
        actionInfo = dispatcher.findDescriptor("GET", request.requestURI!!)!! // named and unnamed params
        params = actionInfo.buildParams(request)
        assertEquals("bar", params[0])
        assertEquals("42", params["id"])
        assertEquals(2, params.size())

        // crud controller
        request = mockRequest("GET", "/crud?name=value")
        actionInfo = dispatcher.findDescriptor("GET", request.requestURI!!)!! // empty route with parameters
        assertEquals(Routes.Crud.Index().javaClass, actionInfo.resourceClass)
        params = actionInfo.buildParams(request)
        assertEquals("value", params["name"])

        request = mockRequest("GET", "/crud/42")
        actionInfo = dispatcher.findDescriptor("GET", request.requestURI!!)!! // named parameter
        params = actionInfo.buildParams(request)
        assertEquals("42", params["id"])

        dispatcher.findDescriptor("POST", "/crud") // models

        request = mockRequest("PUT", "/crud/42")
        actionInfo = dispatcher.findDescriptor("PUT", request.requestURI!!)!! // put
        params = actionInfo.buildParams(request)
        assertEquals("42", params["id"])

        request = mockRequest("DELETE", "/crud/42")
        actionInfo = dispatcher.findDescriptor("DELETE", request.requestURI!!)!! // delete
        params = actionInfo.buildParams(request)
        assertEquals("42", params["id"])
    }
}
