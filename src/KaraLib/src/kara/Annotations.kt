package kara


/** Used to annotate actions that respond to HTTP requests.
 */
@Retention(AnnotationRetention.RUNTIME) annotation class Route(val method : HttpMethod, val route : String, val allowCrossOrigin: String = "")

/** Used to annotate actions that respond to HTTP GET requests.
 */
@Retention(AnnotationRetention.RUNTIME) annotation class Get(val route : String = "")

/** Used to annotate actions that respond to POST requests.
 */
@Retention(AnnotationRetention.RUNTIME) annotation class Post(val route : String = "", val allowCrossOrigin: String = "")

/** Used to annotate actions that respond to PUT requests.
 */
@Retention(AnnotationRetention.RUNTIME) annotation class Put(val route : String = "", val allowCrossOrigin: String = "")

/** Used to annotate actions that respond to DELETE requests.
 */
@Retention(AnnotationRetention.RUNTIME) annotation class Delete(val route : String = "", val allowCrossOrigin: String = "")

/** Used to annotate route objects, representing alternative URL path component.
Default is / for top level root object or lowercased object name for inner objects
*/
@Retention(AnnotationRetention.RUNTIME) annotation class Location(val path: String = "")

/** Used to annotate route objects, for which HttpSession should not be used (service calls, non-browser user agents) */
@Retention(AnnotationRetention.RUNTIME) annotation class NoSession

/** Used to annotate route objects, when you want to use nested functions as routes **/
@Retention(AnnotationRetention.RUNTIME) @Target(AnnotationTarget.CLASS) annotation class Controller(val contentType: String)

/**
 * Used to annotate route objects, when you want to use implementations of class as routes.
 * Only final classes will be treated as implementations of interface controller.
 * You must not have more than one implementation of every annotated interface controller.
 **/
@Retention(AnnotationRetention.RUNTIME) @Target(AnnotationTarget.CLASS) annotation class InterfaceController