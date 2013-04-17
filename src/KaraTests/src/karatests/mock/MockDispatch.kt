package karatests.mock

import karatests.controllers.Routes
import kara.*
import kara.internal.*


val _dispatcher = Dispatcher(scanObjects(array(Routes)))
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
