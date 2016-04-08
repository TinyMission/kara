package kara.tests.mock

import kara.Application
import kara.ApplicationConfig
import kara.ApplicationContext
import kara.internal.scanObjects
import kara.tests.controllers.Routes

object MockApplication : Application(ApplicationConfig(ApplicationConfig::class.java.classLoader)) {

    override val context: ApplicationContext

    init {
        val classLoader = config.appClassloader
        context = ApplicationContext(config, listOf<String>(), classLoader, hashMapOf(), scanObjects(arrayOf(Routes), classLoader))
    }
}

/** Provides a mock dispatch of the given method and url.
 */
fun mockDispatch(httpMethod : String, url : String) : MockHttpServletResponse {
    val request = MockHttpServletRequest(httpMethod, url)
    val response = MockHttpServletResponse()
    MockApplication.context.dispatch(request, response)
    return response
}


/** Creates a HttpServletRequest with the given method and url*/
fun mockRequest(httpMethod : String, url : String) : MockHttpServletRequest {
    val request = MockHttpServletRequest(httpMethod, url)
    return request
}
