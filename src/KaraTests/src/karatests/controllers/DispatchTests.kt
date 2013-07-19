package karatests.controllers

import kotlin.test.*
import kara.*
import kara.internal.*
import karatests.mock.mockRequest
import org.apache.log4j.BasicConfigurator
import org.junit.Before
import org.junit.Test

/** Tests for dispatching routes to get action info. */
class DispatchTests() {
    Before fun setUp() {
        BasicConfigurator.configure()
    }

    Test fun runDispatchTests() {
        val appConfig = AppConfig()
        appConfig["kara.appPackage"] = "karatests.controllers"

        val app = object : Application(appConfig) {}

        val dispatcher = ActionDispatcher(app, scanObjects(array(Routes)))

        var actionInfo = dispatcher.findDescriptor("GET", "/")!!
        assertNotNull(actionInfo)

        fails( {dispatcher.findDescriptor("GET", "/invalid_path")} )


        // foo controller
        actionInfo = dispatcher.findDescriptor("GET", "/foo/bar")!!
        assertEquals(Routes.Foo.Bar().javaClass, actionInfo.requestClass)

        dispatcher.findDescriptor("GET", "/foo/bar/baz")!! // nested route

        dispatcher.findDescriptor("GET", "/foo/foobar") // default action name

        var request = mockRequest("GET", "/foo/bar/list")
        actionInfo = dispatcher.findDescriptor("GET", request.getRequestURI()!!)!! // unnamed param
        var params = actionInfo.buildParams(request)
        assertEquals("bar", params[0])

        request = mockRequest("GET", "/foo/complex/bar/list/42")
        actionInfo = dispatcher.findDescriptor("GET", request.getRequestURI()!!)!! // named and unnamed params
        params = actionInfo.buildParams(request)
        assertEquals("bar", params[0])
        assertEquals("42", params["id"])
        assertEquals(2, params.size())

        // crud controller
        request = mockRequest("GET", "/crud?name=value")
        actionInfo = dispatcher.findDescriptor("GET", request.getRequestURI()!!)!! // empty route with parameters
        assertEquals(Routes.Crud.Index().javaClass, actionInfo.requestClass)
        params = actionInfo.buildParams(request)
        assertEquals("value", params["name"])

        request = mockRequest("GET", "/crud/42")
        actionInfo = dispatcher.findDescriptor("GET", request.getRequestURI()!!)!! // named parameter
        params = actionInfo.buildParams(request)
        assertEquals("42", params["id"])

        dispatcher.findDescriptor("POST", "/crud") // models

        request = mockRequest("PUT", "/crud/42")
        actionInfo = dispatcher.findDescriptor("PUT", request.getRequestURI()!!)!! // put
        params = actionInfo.buildParams(request)
        assertEquals("42", params["id"])

        request = mockRequest("DELETE", "/crud/42")
        actionInfo = dispatcher.findDescriptor("DELETE", request.getRequestURI()!!)!! // delete
        params = actionInfo.buildParams(request)
        assertEquals("42", params["id"])
    }
}
