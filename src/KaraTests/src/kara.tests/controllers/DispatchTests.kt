package kara.tests.controllers

import kara.Application
import kara.ApplicationConfig
import kara.baseLink
import kara.href
import kara.internal.ResourceDispatcher
import kara.internal.scanObjects
import kara.tests.mock.mockDispatch
import kara.tests.mock.mockRequest
import kotlinx.reflection.Serialization
import org.junit.Test
import java.math.BigDecimal
import javax.servlet.http.HttpServletResponse.SC_CREATED
import javax.servlet.http.HttpServletResponse.SC_OK
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/** Tests for dispatching routes to get action info. */
class DispatchTests() {

    @Test
    fun runDispatchTests() {
        val appConfig = ApplicationConfig.loadFrom("src/KaraTests/src/kara.tests/test.conf")

        val app = object : Application(appConfig) {}

        val dispatcher = ResourceDispatcher(app.context, scanObjects(arrayOf(Routes)))

        var actionInfo = dispatcher.findDescriptor("GET", "/")!!
        assertNotNull(actionInfo)

        assertNull(dispatcher.findDescriptor("GET", "/invalid_path"))

        // foo controller
        actionInfo = dispatcher.findDescriptor("GET", "/foo/bar")!!
        assertEquals(Routes.Foo.Bar()::class.simpleName, actionInfo.resourceFun(emptyMap())::class.simpleName)

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
        assertEquals(Routes.Crud.Index()::class.simpleName, actionInfo.resourceFun(emptyMap())::class.simpleName)
        params = actionInfo.buildParams(request)
        assertEquals("value", params["name"])

        // Param function route execution
        assertEquals("", mockDispatch("GET", "/fun/empty").stringOutput())
        val expectedResult = Serialization.serialize(Routes.Function.compute(42, BigDecimal("3.1415")))
        assertEquals(expectedResult, mockDispatch("GET", "/fun/compute/42/3.1415").stringOutput())
        assertEquals(expectedResult, mockDispatch("GET", Routes.Function::compute.href(42, BigDecimal("3.1415"))).stringOutput())

        assertEquals(SC_OK, mockDispatch("GET", Routes.Function::customResultCode.href(SC_OK)).status)
        assertEquals(SC_OK.toString(), mockDispatch("GET", Routes.Function::customResultCode.href(SC_OK)).stringOutput())
        assertEquals(SC_CREATED, mockDispatch("GET", Routes.Function::customResultCode.href(SC_CREATED)).status)
        assertEquals(SC_CREATED.toString(), mockDispatch("GET", Routes.Function::customResultCode.href(SC_CREATED)).stringOutput())


        // crud controller
        request = mockRequest("GET", "/fun?name=value")
        actionInfo = dispatcher.findDescriptor("GET", request.requestURI!!)!! // empty route with parameters
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

        val arrayBaseUrl = Routes.ArrayParam::class.baseLink().href()
        val paramPart = Routes.ArrayParam::arr.name
        assertEquals("foo", mockDispatch("GET", "$arrayBaseUrl?$paramPart=foo").stringOutput())
        assertEquals("foo, bar", mockDispatch("GET", "$arrayBaseUrl?$paramPart=foo&$paramPart=bar").stringOutput())

        val arrayWithNullsURL = Routes.ArrayParam(arrayOf("foo", null, "bar")).href()
        assertEquals("foo, null, bar", mockDispatch("GET", arrayWithNullsURL).stringOutput())
    }

    @Test
    fun runInterfaceDispatchTests() {
        val appConfig = ApplicationConfig.loadFrom("src/KaraTests/src/kara.tests/test.conf")

        val app = object : Application(appConfig) {}

        val dispatcher = ResourceDispatcher(app.context, scanObjects(arrayOf(Routes)))

        val actionInfo = dispatcher.findDescriptor("GET", "interfacecontrollertest/Action")!!
        assertNotNull(actionInfo)

        val stringController = mockDispatch("GET", "interfacecontrollertest/Action").stringOutput()
        assertEquals("It's implementation", stringController)

        val link = Routes.InterfaceControllerTest.SomeInterfaceController::class.baseLink().href()
        assertEquals(link, "/interfacecontrollertest/Action")

    }

    @Test
    fun runInterfaceNotFinalDispatchTests() {
        val appConfig = ApplicationConfig.loadFrom("src/KaraTests/src/kara.tests/test.conf")

        val app = object : Application(appConfig) {}

        val dispatcher = ResourceDispatcher(app.context, scanObjects(arrayOf(Routes)))

        val actionInfo = dispatcher.findDescriptor("GET", "interfacenotfinalcontrollertest/Action")
        assertNull(actionInfo)
    }

    @Test
    fun runInterfaceParamDispatchTests() {
        val appConfig = ApplicationConfig.loadFrom("src/KaraTests/src/kara.tests/test.conf")

        val app = object : Application(appConfig) {}

        val dispatcher = ResourceDispatcher(app.context, scanObjects(arrayOf(Routes)))

        val actionInfo = dispatcher.findDescriptor("GET", "interfaceparamcontrollertest/Action")!!
        assertNotNull(actionInfo)

        val stringController = mockDispatch("GET", "interfaceparamcontrollertest/Action?code=1234").stringOutput()
        assertEquals("It's implementation with param 1234", stringController)

        val link = Routes.InterfaceParamControllerTest.SomeInterfaceController::class.baseLink().href()
        assertEquals(link, "/interfaceparamcontrollertest/Action")

    }
}
