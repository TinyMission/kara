package karatests.mock

import kara.controllers.Dispatcher
import kara.config.AppConfig


val _dispatcher = Dispatcher()

val _mockAppConfig = AppConfig("", "test")

public fun initMockDispatchWithReflection() {
    _mockAppConfig["kara.appPackage"] = "karatests.controllers"
    _dispatcher.initWithReflection(_mockAppConfig)
}

/** Provides a mock dispatch of the given method and url.
 */
public fun mockDispatch(httpMethod : String, url : String) : MockHttpServletResponse {
    val request = MockHttpServletRequest(httpMethod, url)
    val response = MockHttpServletResponse()
    _dispatcher.dispatch(_mockAppConfig, request, response)
    return response
}


/** Creates a HttpServletRequest with the given method and url*/
public fun mockRequest(httpMethod : String, url : String) : MockHttpServletRequest {
    val request = MockHttpServletRequest(httpMethod, url)
    return request
}