package karatests.controllers

import kara.controllers.Dispatcher
import kotlin.test.*
import kara.config.AppConfig
import karatests.mock.mockRequest

/** Tests for dispatching routes to get action info. */
fun runDispatchTests() {

    val appConfig = AppConfig("", "development")
    appConfig["kara.appPackage"] = "karatests.controllers"

    val dispatcher = Dispatcher(Routes)

    var actionInfo = dispatcher.match("GET", "/")!!
    assertNotNull(actionInfo)

    fails( {dispatcher.match("GET", "/invalid_path")} )


    // foo controller
    actionInfo = dispatcher.match("GET", "/foo/bar")!!
    assertEquals(Routes.Foo.Bar().javaClass, actionInfo.requestClass)

    dispatcher.match("GET", "/foo/bar/baz")!! // nested route

    dispatcher.match("GET", "/foo/foobar") // default action name

    var request = mockRequest("GET", "/foo/bar/list")
    actionInfo = dispatcher.match("GET", request.getRequestURI()!!)!! // unnamed param
    var params = actionInfo.getParams(request)
    assertEquals("bar", params[0])

    request = mockRequest("GET", "/foo/complex/bar/list/42")
    actionInfo = dispatcher.match("GET", request.getRequestURI()!!)!! // named and unnamed params
    params = actionInfo.getParams(request)
    assertEquals("bar", params[0])
    assertEquals("42", params["id"])
    assertEquals(2, params.size())

    // crud controller
    request = mockRequest("GET", "/crud?name=value")
    actionInfo = dispatcher.match("GET", request.getRequestURI()!!)!! // empty route with parameters
    assertEquals(Routes.Crud.Index().javaClass, actionInfo.requestClass)
    params = actionInfo.getParams(request)
    assertEquals("value", params["name"])

    request = mockRequest("GET", "/crud/42")
    actionInfo = dispatcher.match("GET", request.getRequestURI()!!)!! // named parameter
    params = actionInfo.getParams(request)
    assertEquals("42", params["id"])

    dispatcher.match("POST", "/crud") // models

    request = mockRequest("PUT", "/crud/42")
    actionInfo = dispatcher.match("PUT", request.getRequestURI()!!)!! // put
    params = actionInfo.getParams(request)
    assertEquals("42", params["id"])

    request = mockRequest("DELETE", "/crud/42")
    actionInfo = dispatcher.match("DELETE", request.getRequestURI()!!)!! // delete
    params = actionInfo.getParams(request)
    assertEquals("42", params["id"])
}
