package karatests.controllers

import kara.controllers.Dispatcher
import kotlin.test.*
import kara.config.AppConfig

/** Tests for dispatching routes to get action info. */
fun runDispatchTests(args : Array<String>) {

    val appConfig = AppConfig("", "development")
    appConfig["kara.appPackage"] = "karatests.controllers"

    val dispatcher = Dispatcher()
    dispatcher.initWithReflection(appConfig)

    var actionInfo = dispatcher.match("GET", "/")!!
    assertNotNull(actionInfo)

    fails( {dispatcher.match("GET", "/invalid_path")} )


    // foo controller
    actionInfo = dispatcher.match("GET", "/foo/bar")!!
    assertEquals(FooController().javaClass, actionInfo.controller.javaClass)

    dispatcher.match("GET", "/foo/bar/baz")!! // nested route

    dispatcher.match("GET", "/foo/foobar") // default action name

    actionInfo = dispatcher.match("GET", "/foo/bar/list")!! // unnamed param
    var params = actionInfo.getParams("/foo/bar/list", "")
    assertEquals("bar", params[0])

    actionInfo = dispatcher.match("GET", "/foo/complex/bar/list/42")!! // named and unnamed params
    params = actionInfo.getParams("/foo/complex/bar/list/42", "")
    assertEquals("bar", params[0])
    assertEquals("42", params["id"])
    assertEquals(2, params.size())

    // crud controller
    actionInfo = dispatcher.match("GET", "/crud?name=value")!! // empty route with parameters
    assertEquals(CrudController().javaClass, actionInfo.controller.javaClass)
    params = actionInfo.getParams("/crud?name=value", "")
    assertEquals("value", params["name"])

    actionInfo = dispatcher.match("GET", "/crud/42")!! // named parameter
    params = actionInfo.getParams("/crud/42", "")
    assertEquals("42", params["id"])

    dispatcher.match("POST", "/crud") // models

    actionInfo = dispatcher.match("PUT", "/crud/42")!! // put
    params = actionInfo.getParams("/crud/42", "")
    assertEquals("42", params["id"])

    actionInfo = dispatcher.match("DELETE", "/crud/42")!! // delete
    params = actionInfo.getParams("/crud/42", "")
    assertEquals("42", params["id"])
}