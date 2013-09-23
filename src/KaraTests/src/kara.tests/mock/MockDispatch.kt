package kara.tests.mock

import kara.tests.controllers.Routes
import kara.*
import kara.internal.*


val _context = Routes.javaClass.getClassLoader()!!.let { classLoader -> ApplicationContext(listOf<String>(), classLoader, scanObjects(array(Routes), classLoader)) }

/** Provides a mock dispatch of the given method and url.
 */
public fun mockDispatch(httpMethod : String, url : String) : MockHttpServletResponse {
    val request = MockHttpServletRequest(httpMethod, url)
    val response = MockHttpServletResponse()
    _context.dispatch(request, response)
    return response
}


/** Creates a HttpServletRequest with the given method and url*/
public fun mockRequest(httpMethod : String, url : String) : MockHttpServletRequest {
    val request = MockHttpServletRequest(httpMethod, url)
    return request
}
