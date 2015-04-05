package kara.tests.mock

import kara.tests.controllers.Routes
import kara.*
import kara.internal.*

object MockApplication : Application(ApplicationConfig()) {

    override val context: ApplicationContext

    init {
        val classLoader = javaClass.getClassLoader()!!
        context = ApplicationContext(this, listOf<String>(), classLoader, scanObjects(array(Routes), classLoader))
    }
}

/** Provides a mock dispatch of the given method and url.
 */
public fun mockDispatch(httpMethod : String, url : String) : MockHttpServletResponse {
    val request = MockHttpServletRequest(httpMethod, url)
    val response = MockHttpServletResponse()
    MockApplication.context.dispatch(request, response)
    return response
}


/** Creates a HttpServletRequest with the given method and url*/
public fun mockRequest(httpMethod : String, url : String) : MockHttpServletRequest {
    val request = MockHttpServletRequest(httpMethod, url)
    return request
}
