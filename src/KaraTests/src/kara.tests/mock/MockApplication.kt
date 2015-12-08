package kara.tests.mock

import kara.tests.controllers.Routes
import kara.*
import kara.internal.*

object MockApplication : Application(ApplicationConfig(ApplicationConfig::class.java.classLoader)) {

    override val context: ApplicationContext

    init {
        val classLoader = config.appClassloader
        context = ApplicationContext(config, listOf<String>(), classLoader, hashMapOf(), scanObjects(arrayOf(Routes), classLoader))
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
