package karatests.mock

import kara.controllers.Dispatcher
import kara.config.AppConfig
import karatests.controllers.Routes


val _dispatcher = Dispatcher(Routes)
val _mockAppConfig = AppConfig("", "test")

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
