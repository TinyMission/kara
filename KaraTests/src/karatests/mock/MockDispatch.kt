package karatests.mock

import kara.controllers.Dispatcher


val _dispatcher = Dispatcher("karatests.controllers")

public fun initMockDispatchWithReflection() {
    _dispatcher.initWithReflection()
}

/** Provides a mock dispatch of the given method and url.
 */
public fun mockDispatch(httpMethod : String, url : String) : MockHttpServletResponse {
    val request = MockHttpServletRequest(httpMethod, url)
    val response = MockHttpServletResponse()
    _dispatcher.dispatch(request, response)
    return response
}